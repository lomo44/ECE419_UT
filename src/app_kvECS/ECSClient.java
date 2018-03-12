package app_kvECS;

import common.command.KVCommand;
import common.communication.KVCommunicationModule;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;
import common.metadata.KVMetadataController;
import common.networknode.KVNetworkNode;
import common.networknode.KVStorageNode;
import common.zookeeper.*;

import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.Level;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecs.ECSNode;
import ecs.IECSNode;
import logger.KVOut;
import org.apache.zookeeper.KeeperException;

/*
 * mode				0:PERSISTENT
 * 				    1:EPHEMERAL
 * 					2:PERSISTENT_SEQUENTIAL
 * 					3.EPHEMERAL_SEQUENTIAL
 */


public class ECSClient implements IECSClient {

    private final static String ZOOKEEPER_SERVER_HOSTNAME = "localhost";
    private final static String LOCAL_HOST_IP = "127.0.0.1";
    private final static String DEPLOYED_EXECUTABLE_PATH = "~/ECE419_UT/m2-server.jar";
    private final static String CONFIG_PATH = "~/ECE419_UT/ecs.config";
    private final static String PROMPT = "ECSClient: ";
    private final static Pattern config_parser = Pattern.compile("(.*) (.*) (\\d*)");


    private List<KVStorageNode> sleepingServer;
    private List<KVStorageNode> runningServer = new ArrayList<>();
    private HashMap<KVNetworkNode, KVCommunicationModule> controlChannelMap = new HashMap<>();
    private Map<String, KVStorageNode> nameKVStorageNodeMap = new HashMap<>();
    private TreeMap<String, IECSNode> nameECSNodeMap = new TreeMap<>();
    private List<Process> createdProcess = new ArrayList<>();


    private static KVOut kv_out = new KVOut("ECS");
    private Scanner keyboard;
    private String zkhost;
    private int zkport;
    private ZKadmin zkAdmin;
    private boolean isRunning = false;
    private ECSClientCommandLineParser cmdParser = new ECSClientCommandLineParser();

    private KVMetadataController metadataController = new KVMetadataController();

    public ECSClient(String host, int port) throws IOException{
    		this(CONFIG_PATH,host,port);
    }
    
    public ECSClient(String configFile, String host, int port) throws IOException {
        this(configFile,host,port,System.in);

    }

    public ECSClient(String configFile, String host, int port, InputStream inputStream) throws IOException {
        keyboard = new Scanner(inputStream);
        zkhost = host;
        zkport = port;
        String hostport = zkhost + ":" + Integer.toString(zkport);
        zkAdmin = new ZKadmin(hostport, kv_out);
        sleepingServer = getAvailableNodesFromConfig(configFile);
    }

    @Override
    public boolean start() throws IOException {
        for (KVStorageNode node: runningServer
                ) {
            if(!startNode(node)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean stop() throws IOException {
        for (KVStorageNode node: runningServer
                ) {
            if(!stopNode(node)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean shutdown() throws IOException, KeeperException, InterruptedException {
        for (KVStorageNode node: runningServer
             ) {
            if(!shutdownNode(node)){
                return false;
            }
        }
        zkAdmin.close();
        for (Process process: createdProcess
             ) {
            process.destroyForcibly();
        }
        isRunning = false;

        return true;
    }


    public boolean clearAllStorage() throws IOException {
        for (KVStorageNode node: runningServer
                ) {
            if(!clearStorage(node)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeNodes(Collection<String> nodeNames) {
        for(String name: nodeNames){
            try {
                removeNode(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean removeNode(String nodeNames) throws IOException {
        KVStorageNode node = this.nameKVStorageNodeMap.get(nodeNames);
        if(!shutdownNode(node)){
            return false;
        }
        return true;
    }

    @Override
    public Map<String, IECSNode> getNodes() {
        return nameECSNodeMap;
    }

    public TreeMap<String, IECSNode> getNameECSNodeMap() {
        return nameECSNodeMap;
    }

    @Override
    public IECSNode addNode(String cacheStrategy, int cacheSize) {
        return addNodes(1,cacheStrategy,cacheSize).iterator().next();
    }

    @Override
    public Collection<IECSNode> addNodes(int count, String cacheStrategy, int cacheSize) {
        // Select nodes to setup
        List<KVStorageNode> selectedNode = selectServerToSetup(count);
        // Setup nodes on zookeeper
        zkAdmin.setupNodes(selectedNode, cacheStrategy, cacheSize);
        try {
            // Try to start server via ssh
            startServers(selectedNode);
            // Wait for nodes to come in
        } catch (Exception e) {
            System.out.println("add nodes timed out");
            e.printStackTrace();
        }
        runningServer.addAll(selectedNode);
        // Add selected nodes
        try {
            if(awaitNodes(count,15*1000)){
                metadataController.addStorageNodes(selectedNode);
                zkAdmin.broadcastMetadata(selectedNode,metadataController.getMetaData());
                return convertToECSNode(selectedNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<IECSNode> setupNodes(int count, String cacheStrategy, int cacheSize) {
        List<KVStorageNode> selectedNode = selectServerToSetup(count);
        zkAdmin.setupNodes(selectedNode,cacheStrategy,cacheSize);
        return convertToECSNode(selectedNode);
    }

    private Collection<IECSNode> convertToECSNode(List<KVStorageNode> list){
        Collection<IECSNode> ret = new HashSet<>();
        for (KVStorageNode node : list
                ) {
            ret.add(new ECSNode(node));
        }
        return ret;
    }

    @Override
    public boolean awaitNodes(int count, int timeout) throws Exception {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis()-startTime <= timeout){
            if(zkAdmin.getCurrentSetupNodesNames().size() >= count){
                return true;
            }
        }
        return false;
    }

    //random pick 'count' servers from idle server pool
    private List<KVStorageNode> selectServerToSetup(int count) {
        List<KVStorageNode> serverToAdd = new ArrayList<>();
        while(serverToAdd.size()!=count && sleepingServer.size()!=0){
            int index = ThreadLocalRandom.current().nextInt(0, sleepingServer.size());
            serverToAdd.add(sleepingServer.get(index));
            sleepingServer.remove(index);
        }
        return serverToAdd;
    }


    @Override
    public IECSNode getNodeByKey(String Key) {
        return metadataController.getResponsibleStorageNode(Key).toECSNode();
    }

    private void startServers(List<KVStorageNode> nodes) throws IOException, InterruptedException {
        for(KVStorageNode node : nodes){
            runServerViaSSH(node.getserverName(),ZOOKEEPER_SERVER_HOSTNAME,zkport);
        }
    }

    private List<KVStorageNode> getAvailableNodesFromConfig(String file) throws IOException {
        List<KVStorageNode> ret = new ArrayList<>();
        FileReader config = new FileReader(file);
        BufferedReader readbuf = new BufferedReader(config);
        String configLine = readbuf.readLine();
        while (configLine != null) {
            Matcher match = config_parser.matcher(configLine);
            if(match.find()){
                // hostname portnumber servername
                KVStorageNode newNode = new KVStorageNode(match.group(2),Integer.parseInt(match.group(3)),match.group(1));
                ret.add(newNode);
                nameKVStorageNodeMap.put(newNode.getserverName(),newNode);
                nameECSNodeMap.put(newNode.getserverName(),newNode.toECSNode());
            }
            configLine = readbuf.readLine();
        }
        readbuf.close();
        return ret;
    }

    private void runServerViaSSH(String name, String zkhost, int zkport) throws IOException, InterruptedException {
        String[] args = new String[]{"ssh", "-n", LOCAL_HOST_IP, "nohup",
                "java", "-jar",DEPLOYED_EXECUTABLE_PATH, name, zkhost, Integer.toString(zkport)};
        System.out.println("start init server...");
        Process builder = new ProcessBuilder().inheritIO().command(args).start();
        createdProcess.add(builder);
        //pb.waitFor();
    }

    private void run(){
        kv_out.println_debug("Client started.");
        isRunning = true;
        while (isRunning) {
            System.out.print(PROMPT);
            KVCommand<ECSClient> cmdInstance = cmdParser.getParsedCommand(keyboard.nextLine());
            if (cmdInstance != null) {
                // Command line correctly parsed
                cmdInstance.handleResponse(executeCommand(cmdInstance));
            } else {
                cmdParser.printHelpMessages();
            }
        }
        kv_out.println_debug("Client stopped.");
    }

    private KVJSONMessage executeCommand(KVCommand cmdInstance){
        return cmdInstance.execute(this);
    }

    public boolean startNode(KVStorageNode node) throws IOException {
        if(!controlChannelMap.containsKey(node)){
            controlChannelMap.put(node,node.createCommunicationModule());
        }
        KVCommunicationModule communicationModule = controlChannelMap.get(node);
        KVJSONMessage msg = new KVJSONMessage();
        msg.setExtendStatus(eKVExtendStatusType.SERVER_START);
        communicationModule.send(msg);
        KVJSONMessage response = communicationModule.receiveMessage();
        if(response.getExtendStatusType() == eKVExtendStatusType.START_SUCCESS) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean stopNode(KVStorageNode node) throws IOException {
        if(!controlChannelMap.containsKey(node)){
            controlChannelMap.put(node,node.createCommunicationModule());
        }
        KVCommunicationModule communicationModule = controlChannelMap.get(node);
        KVJSONMessage msg = new KVJSONMessage();
        msg.setExtendStatus(eKVExtendStatusType.SERVER_STOP);
        communicationModule.send(msg);
        KVJSONMessage response = communicationModule.receiveMessage();
        if(response.getExtendStatusType() == eKVExtendStatusType.STOP_SUCCESS) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean clearStorage(KVNetworkNode node) throws IOException{
        if(!controlChannelMap.containsKey(node)){
            controlChannelMap.put(node,node.createCommunicationModule());
        }
        KVCommunicationModule communicationModule = controlChannelMap.get(node);
        KVJSONMessage msg = new KVJSONMessage();
        msg.setExtendStatus(eKVExtendStatusType.CLEAR_STORAGE);
        communicationModule.send(msg);
        KVJSONMessage response = communicationModule.receiveMessage();
        if(response.getExtendStatusType() == eKVExtendStatusType.CLEAR_SUCCESS) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean shutdownNode(KVNetworkNode node) throws IOException{
        if(!controlChannelMap.containsKey(node)){
            controlChannelMap.put(node,node.createCommunicationModule());
        }
        KVCommunicationModule communicationModule = controlChannelMap.get(node);
        KVJSONMessage msg = new KVJSONMessage();
        msg.setExtendStatus(eKVExtendStatusType.SERVER_SHUTDOWN);
        communicationModule.send(msg);
        return true;
    }

    public static void main(String[] args) throws IOException {
        kv_out.enableLog("logs/ECS.log", Level.ALL);
        String zkhost = "localhost";
        int zkport = 2181;

        ECSClient admin = new ECSClient(args[0], zkhost, zkport);
        admin.run();
//        ZKadmin zkAdmin = admin.zkAdmin;
//        zkAdmin.connect();
//        zkAdmin.setupServer();
//        admin.addNodes(1, "FIFO", 10);
////    		try {
////				admin.runServer("id_rsa",
////						  "nintengao@192.168.2.10",
////						  "~/ECE419_UT/m2-server.jar",
////						  "50000","10","FIFO");
////			} catch (IOException | InterruptedException e1) {
////				// TODO Auto-generated catch block
////				e1.printStackTrace();
////			}
////    		try {
////				Thread.sleep(600000);
////			} catch (InterruptedException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//        zkAdmin.disconnect();
    }
}



