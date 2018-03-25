package app_kvECS;

import app_kvServer.KVServerConfig;
import common.command.KVCommand;
import common.communication.KVCommunicationModule;
import common.communication.KVCommunicationModuleSet;
import common.enums.*;
import common.messages.KVClusterOperationMessage;
import common.messages.KVJSONMessage;
import common.metadata.KVMetadataController;
import common.networknode.KVNetworkNode;
import common.networknode.KVStorageCluster;
import common.networknode.KVStorageNode;
import common.zookeeper.*;

import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.Level;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecs.ECSNode;
import ecs.IECSNode;
import logger.KVOut;

/*
 * mode				0:PERSISTENT
 * 				    1:EPHEMERAL
 * 					2:PERSISTENT_SEQUENTIAL
 * 					3.EPHEMERAL_SEQUENTIAL
 */


public class ECSClient implements IECSClient {

    private final static String ZOOKEEPER_SERVER_HOSTNAME = "localhost";
    private final static String LOCAL_HOST_IP = "127.0.0.1";

    private final static String CONFIG_PATH = "~/ECE419_UT/ecs.config";
    private final static String PROMPT = "ECSClient: ";
    private final static Pattern config_parser = Pattern.compile("(.*) (.*) (\\d*)");

    private String deployedServerJarPath = "~/ECE419_UT/m2-server.jar";
    private HashMap<String, KVStorageNode> sleepingServer = new HashMap<>();
    private HashMap<String, KVStorageNode> runningServer = new HashMap<>();
    private List<Process> createdProcess = new ArrayList<>();
    private TreeMap<String, IECSNode> nameECSNodeMap = new TreeMap<>();
    private KVCommunicationModuleSet serverCommunicationModules = new KVCommunicationModuleSet();
    private eKVNodeCreationMode nodeCreationMode = eKVNodeCreationMode.INDIVIDUAL_CLUSTER;
    private KVMetadataController metadataController = new KVMetadataController();


    private static KVOut kv_out = new KVOut("ECS");
    private Scanner keyboard;
    private String zkhost;
    private int zkport;
    private ZKadmin zkAdmin;
    private boolean isRunning = false;
    private ECSClientCommandLineParser cmdParser = new ECSClientCommandLineParser();



    public ECSClient(String host, int port) throws IOException{
    		this(host,port,CONFIG_PATH);
    }
    
    public ECSClient(String host,int port,String configFile) throws IOException {
        this(configFile,host,port,System.in);

    }

    public ECSClient(String configFile, String host, int port, InputStream inputStream) throws IOException {
        keyboard = new Scanner(inputStream);
        zkhost = host;
        zkport = port;
        String hostport = zkhost + ":" + Integer.toString(zkport);
        zkAdmin = new ZKadmin(hostport, kv_out);
        sleepingServer = getAvailableNodesFromConfig(configFile);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean start(){
        for (KVStorageNode node: runningServer.values()
                ) {
            if(!startNode(node)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean stop() throws IOException {
        for (KVStorageNode node: runningServer.values()
                ) {
            if(!stopNode(node)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean shutdown(){
        boolean ret = true;
        for (KVStorageNode node: runningServer.values()
             ) {
            try {
                if(!shutdownNode(node)){
                    ret = false;
                }
            } catch (IOException e) {
                ret = false;
            }
        }
        try {
            zkAdmin.close();
        } catch (Exception e) {
           ret = false;
        }
        for (Process process: createdProcess
             ) {
            process.destroyForcibly();
        }
        isRunning = false;
        this.serverCommunicationModules.close();
        return ret;
    }


    public boolean clearAllStorage() throws IOException {
        for (KVStorageNode node: runningServer.values()
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

    public boolean removeNode(String nodeName) throws IOException {
        KVStorageNode node = this.runningServer.get(nodeName);
        if(node!=null){
            if(!shutdownNode(node)){
                return false;
            }
            this.runningServer.remove(node);
            this.sleepingServer.put(node.getUID(),node);
            Collection<KVStorageNode> relevantNodes = metadataController.getReleventNodes(nodeName);
            for(KVStorageNode relevantNode : relevantNodes){
                switch (relevantNode.getNodeType()){
                    case STORAGE_NODE:{
                        metadataController.removeStorageNode(node.getUID());
                        break;
                    }
                    case STORAGE_CLUSTER:{
                        KVStorageCluster cluster = (KVStorageCluster) relevantNode;
                        cluster.removeNodeByUID(nodeName);
                        if(cluster.isEmpty()){
                            removeCluster(cluster.getUID());
                        }
                        break;
                    }
                }
            }
            zkAdmin.broadcastMetadata(runningServer.values(),metadataController.getMetaData());
        }
        zkAdmin.removeNodeInZookeeper(nodeName);
        return true;
    }

    @Override
    public Map<String, IECSNode> getNodes() {
        return nameECSNodeMap;
    }

    @Override
    public IECSNode addNode(String cacheStrategy, int cacheSize) {
        return addNodes(1,cacheStrategy,cacheSize).iterator().next();
    }
    @Override
    public Collection<IECSNode> addNodes(int count, String cacheStrategy, int cacheSize) {
        return addNodes(count,cacheStrategy,cacheSize,"");
    }
    public Collection<IECSNode> addNodes(int count, String cacheStrategy, int cacheSize, String clusterName){
        List<KVStorageNode> selectedNodes = selectServerToSetup(count);
        List<KVServerConfig> configs = new ArrayList<>();
        for(KVStorageNode node : selectedNodes){
            configs.add(createServerConfig(node,cacheStrategy,cacheSize,clusterName));
        }
        return convertToECSNode(addNodes(selectedNodes,configs));
    }
    public List<KVStorageNode> addNodes(List<KVStorageNode> selectedNodes, List<KVServerConfig> configs){
        // Setup nodes on zookeeper
        List<KVStorageNode> createdNode = setupNodes(selectedNodes,configs);
        if(createdNode!=null){
            // Try to start server via ssh
            List<Process> addedProcess = startServers(selectedNodes);
            // Wait for nodes to come in
            if(addedProcess!=null){
                // Add selected nodes
                try {
                    if(awaitNodes(selectedNodes.size(),15*1000)){
                        for(KVStorageNode readyNode : selectedNodes){
                            runningServer.put(readyNode.getUID(),readyNode);
                        }
                        createdProcess.addAll(addedProcess);
                        zkAdmin.broadcastMetadata(selectedNodes,metadataController.getMetaData());
                        return selectedNodes;
                    }
                    else {
                        for(Process process:addedProcess){
                            process.destroyForcibly();
                        }
                        rewindeNodes(createdNode);
                        return null;
                    }
                } catch (Exception e) {
                    return null;
                }
            }
            else{
               rewindeNodes(createdNode);
               return null;
            }
        }
        return null;
    }
    @Override
    public Collection<IECSNode> setupNodes(int count, String cacheStrategy, int cacheSize) {
        List<KVStorageNode> selectedNodes = selectServerToSetup(count);
        List<KVServerConfig> configs = new ArrayList<>();
        for(KVStorageNode node : selectedNodes){
            configs.add(createServerConfig(node,cacheStrategy,cacheSize,""));
        }
        List<KVStorageNode> createdNodes = setupNodes(selectedNodes,configs);
        if(createdNodes!=null){
            return convertToECSNode(selectedNodes);
        }
        return null;
    }
    private List<KVStorageNode> setupNodes(List<KVStorageNode> nodes, List<KVServerConfig> configs){
        int serverCount = 0;
        List<KVStorageNode> ret = new ArrayList<>();
        for(KVStorageNode node : nodes){
            KVServerConfig config = configs.get(serverCount);
            if(config.getBelongedCluster()==null){
                HashSet<String> clusters = new HashSet<>();
                switch (nodeCreationMode){
                    case INDIVIDUAL_CLUSTER:{
                        String targetClusterName = "cluster"+metadataController.getStorageNodes().size();
                        switch (createCluster(targetClusterName)){
                            case CREATED:{
                                KVStorageCluster targetClusterNode = (KVStorageCluster) metadataController.getStorageNode(targetClusterName);
                                targetClusterNode.addNode(node);
                                ret.add(node);
                                ret.add(targetClusterNode);
                                clusters.add(targetClusterName);
                                break;
                            }
                            case EXIST:{
                                KVStorageCluster targetClusterNode = (KVStorageCluster) metadataController.getStorageNode(targetClusterName);
                                targetClusterNode.addNode(node);
                                ret.add(node);
                                clusters.add(targetClusterName);
                                break;
                            }
                            case INVALID:{
                                rewindeNodes(ret);
                                return null;
                            }
                        }
                        break;
                    }
                    case FREE_NODE:{
                        this.metadataController.addStorageNode(node);
                        break;
                    }
                    case JOIN_CLUSTER_MAX_SIZE_3:{
                        //TODO:
                    }
                }
                config.setBelongedCluster(clusters);
            }
            zkAdmin.setupNodeInZookeeper(node,configs.get(serverCount));
            serverCount++;
        }
        for(KVStorageNode node : ret){
            if(node.getNodeType()==eKVNetworkNodeType.STORAGE_NODE){
                this.runningServer.put(node.getUID(),node);
            }
        }
        return ret;
    }

    private void rewindeNodes(List<KVStorageNode> nodes){
        for(KVStorageNode node: nodes){
            switch (node.getNodeType()){
                case STORAGE_CLUSTER:{
                    removeCluster(node.getHostName());
                    break;
                }
                case STORAGE_NODE:{
                    try {
                        removeNode(node.getUID());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    public KVServerConfig createServerConfig(KVStorageNode node, String cacheStrategy, int cacheSize, String targetCluster){
        KVServerConfig config = new KVServerConfig();
        config.setCacheSize(cacheSize);
        config.setCacheStratagy(cacheStrategy);
        config.setServerPort(node.getPortNumber());
        config.setServerHostAddress(node.getHostName());
        config.setServerName(node.getUID());
        // initialize cluster information
        return config;
    }

    private Collection<IECSNode> convertToECSNode(List<KVStorageNode> list){
        if(list==null){
            return null;
        }
        else {
            Collection<IECSNode> ret = new HashSet<>();
            for (KVStorageNode node : list
                    ) {
                ret.add(new ECSNode(node));
            }
            return ret;
        }
    }

    @Override
    public boolean awaitNodes(int count, int timeout){
        boolean ret = false;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis()-startTime <= timeout){
            Set<String> curretSetupNodesNames = zkAdmin.getCurrentSetupNodesNames();
            for(String name: curretSetupNodesNames){
                if(sleepingServer.containsKey(name)){
                    runningServer.put(name,sleepingServer.get(name));
                    sleepingServer.remove(name);
                }
            }
            if(curretSetupNodesNames.size() >= count){
                ret = true;
                break;
            }
        }
        //this.metadataController.getMetaData().print();
        zkAdmin.broadcastMetadata(runningServer.values(),this.metadataController.getMetaData());
        return ret;
    }

    //random pick 'count' servers from idle server pool
    public List<KVStorageNode> selectServerToSetup(int count) {
        List<KVStorageNode> serverToAdd = new ArrayList<>();
        while(serverToAdd.size()!=count && sleepingServer.size()!=0){
            int index = ThreadLocalRandom.current().nextInt(0, sleepingServer.size());
            for(String uid : sleepingServer.keySet()){
                if(index==0){
                    serverToAdd.add(sleepingServer.get(uid));
                    sleepingServer.remove(uid);
                    break;
                }
                index--;
            }
        }
        return serverToAdd;
    }

    @Override
    public IECSNode getNodeByKey(String Key) {
        return metadataController.getResponsibleStorageNode(Key).toECSNode();
    }

    private List<Process> startServers(List<KVStorageNode> nodes){
        List<Process> ret = new ArrayList<>();
        for(KVStorageNode node : nodes){
            try {
                ret.add(runServerViaSSH(node.getUID(),ZOOKEEPER_SERVER_HOSTNAME,zkport));
            } catch (IOException e) {
                for(Process process:ret){
                    process.destroyForcibly();
                }
                return null;
            }
        }
        return ret;
    }

    private HashMap<String, KVStorageNode> getAvailableNodesFromConfig(String file) throws IOException {
        HashMap<String, KVStorageNode> ret = new HashMap<>();
        FileReader config = new FileReader(file);
        BufferedReader readbuf = new BufferedReader(config);
        String configLine = readbuf.readLine();
        while (configLine != null) {
            Matcher match = config_parser.matcher(configLine);
            if(match.find()){
                // hostname portnumber servername
                KVStorageNode newNode = new KVStorageNode(match.group(2),Integer.parseInt(match.group(3)),match.group(1));
                ret.put(newNode.getUID(),newNode);
                nameECSNodeMap.put(newNode.getUID(),newNode.toECSNode());
            }
            configLine = readbuf.readLine();
        }
        readbuf.close();
        return ret;
    }

    private Process runServerViaSSH(String name, String zkhost, int zkport) throws IOException {
        String[] args = new String[]{"ssh", "-n", LOCAL_HOST_IP, "nohup",
                "java", "-jar", deployedServerJarPath, name, zkhost, Integer.toString(zkport)};
        System.out.println("start init server...");
        Process builder = new ProcessBuilder().inheritIO().command(args).start();
        return builder;
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

    public boolean startNode(KVStorageNode node){
        if(!serverCommunicationModules.containsKey(node)){
            try {
                serverCommunicationModules.put(node,node.createCommunicationModule());
            } catch (IOException e) {
                return false;
            }
        }
        KVCommunicationModule communicationModule = serverCommunicationModules.get(node);
        KVJSONMessage msg = new KVJSONMessage();
        msg.setExtendStatus(eKVExtendStatusType.SERVER_START);
        try {
            communicationModule.send(msg);
            KVJSONMessage response = communicationModule.receive();
            if(response.getExtendStatusType() == eKVExtendStatusType.START_SUCCESS) {
                return true;
            }
            else {
                kv_out.println_error("Failed to start server");
                return false;
            }
        } catch (SocketException e) {
            kv_out.println_error("Failed to start server, socket exception");
            return false;
        }
    }

    public boolean stopNode(KVStorageNode node) throws IOException {
        if(!serverCommunicationModules.containsKey(node)){
            serverCommunicationModules.put(node,node.createCommunicationModule());
        }
        KVCommunicationModule communicationModule = serverCommunicationModules.get(node);
        KVJSONMessage msg = new KVJSONMessage();
        msg.setExtendStatus(eKVExtendStatusType.SERVER_STOP);
        communicationModule.send(msg);
        KVJSONMessage response = communicationModule.receive();
        if(response.getExtendStatusType() == eKVExtendStatusType.STOP_SUCCESS) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean leaveCluster(String clusterName, String nodeName){
        boolean ret = false;
        KVClusterOperationMessage msg = new KVClusterOperationMessage();
        msg.setOperationType(eKVClusterOperationType.EXIT);
        msg.setTargetCluster("clusters/"+clusterName);
        KVStorageNode node = this.metadataController.getStorageNode(nodeName);
        KVJSONMessage response = null;
        KVStorageCluster cluster = (KVStorageCluster) this.metadataController.getStorageNode(clusterName);
        if(node!=null&&cluster!=null &&
                node.getNodeType()==eKVNetworkNodeType.STORAGE_NODE && cluster.getNodeType() == eKVNetworkNodeType.STORAGE_CLUSTER){
            if(node.getNodeType()== eKVNetworkNodeType.STORAGE_NODE){
                response = serverCommunicationModules.syncSend(msg.toKVJSONMessage(),node);
                if(response.getExtendStatusType() == eKVExtendStatusType.REPLICA_OK){
                    ret = true;
                }
            }
            if(ret){
                /**
                 * Check if the cluster is empty, if it is then remove the cluster from the metadata
                 */
                cluster.removeNodeByUID(nodeName);
                if(cluster.getNumOfMembers()==0){
                    removeCluster(clusterName);
                }
            }
        }
        return ret;
    }

    /**
     * Let node join the cluster. If the cluster does not exist, it will try to create the cluster
     * @param clusterName cluster name
     * @param nodeName node name
     * @return true if success, fail if not
     */
    public boolean joinCluster(String clusterName, String nodeName){
        boolean ret = false;
        KVClusterOperationMessage msg = new KVClusterOperationMessage();
        msg.setOperationType(eKVClusterOperationType.JOIN);
        msg.setTargetCluster("clusters/"+clusterName);
        KVStorageNode node = this.metadataController.getStorageNode(nodeName);
        KVJSONMessage response;
        eKVClusterStatus clusterStatus = createCluster(clusterName);
        if(clusterStatus==eKVClusterStatus.CREATED || clusterStatus==eKVClusterStatus.EXIST){
            if(node!= null && node.getNodeType()== eKVNetworkNodeType.STORAGE_NODE){
                response = serverCommunicationModules.syncSend(msg.toKVJSONMessage(),node);
                if(response.getExtendStatusType() == eKVExtendStatusType.REPLICA_OK){
                    ret = true;
                }
                else{
                    // roll back cluster creation if it is the first time create the cluster
                    if(clusterStatus==eKVClusterStatus.CREATED){
                        removeCluster(clusterName);
                    }
                }
            }
        }
        else{
            return false;
        }
        return ret;
    }

    /**
     * Create a cluster using a name
     * @param clusterName cluster name
     * @return CREATED if cluster is newly created, EXIST if cluster already exists, INVALID if failed.
     */
    public eKVClusterStatus createCluster(String clusterName){
        if(metadataController.getStorageNode(clusterName)==null){
            // Create a new cluster
            try {
                zkAdmin.createCluster(clusterName);
            } catch (Exception e) {
                return eKVClusterStatus.INVALID;
            }
            KVStorageCluster newCluster = new KVStorageCluster(clusterName);
            this.metadataController.addStorageNode(newCluster);
            return eKVClusterStatus.CREATED;
        }
        return eKVClusterStatus.EXIST;
    }

    public eKVClusterStatus removeCluster(String clusterName){
        if(metadataController.getStorageNode(clusterName)!=null){
            // Create a new cluster
            try {
                zkAdmin.removeCluster(clusterName);
            } catch (Exception e) {
                return eKVClusterStatus.EXIST;
            }
            metadataController.removeStorageNode(clusterName);
            return eKVClusterStatus.REMOVED;
        }
        return eKVClusterStatus.INVALID;
    }


    public boolean clearStorage(KVNetworkNode node) throws IOException{
        if(!serverCommunicationModules.containsKey(node)){
            serverCommunicationModules.put(node,node.createCommunicationModule());
        }
        KVCommunicationModule communicationModule = serverCommunicationModules.get(node);
        KVJSONMessage msg = new KVJSONMessage();
        msg.setExtendStatus(eKVExtendStatusType.CLEAR_STORAGE);
        communicationModule.send(msg);
        KVJSONMessage response = communicationModule.receive();
        if(response.getExtendStatusType() == eKVExtendStatusType.CLEAR_SUCCESS) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean shutdownNode(KVNetworkNode node) throws IOException{
        if(!serverCommunicationModules.containsKey(node)){
            serverCommunicationModules.put(node,node.createCommunicationModule());
        }
        KVCommunicationModule communicationModule = serverCommunicationModules.get(node);
        KVJSONMessage msg = new KVJSONMessage();
        msg.setExtendStatus(eKVExtendStatusType.SERVER_SHUTDOWN);
        communicationModule.send(msg);
        return true;
    }

    public void setDeployedServerJarPath(String deployedServerJarPath) {
        this.deployedServerJarPath = deployedServerJarPath;
    }

    public static void main(String[] args) throws IOException {
        kv_out.enableLog("logs/ECS.log", Level.ALL);
        String zkhost = "localhost";
        int zkport = 2181;
        if(args.length!=1){
            System.out.printf("Missing path to ECS.config, exiting...\n");
        }
        else{
            ECSClient admin = new ECSClient(zkhost, zkport,args[0]);
            admin.run();
        }
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



