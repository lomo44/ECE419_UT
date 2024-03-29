package testing.IntegrationTests;


import app_kvClient.KVClient;
import app_kvECS.ECSClient;
import app_kvServer.KVServer;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import common.enums.eKVLogLevel;
import common.messages.KVJSONMessage;
import common.KVMessage;
import ecs.IECSNode;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import utility.KVPutGetGenerator;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ZookeeperTests extends TestCase{

    public static final String ECS_CONFIG_PATH = "./ecs.config";
    public static final String ZK_HOST_NAME = "localhost";
    public static final int ZK_PORT_NUMBER = 2181;
    public static final String ZK_DATA_DIR = "./tmp/zookeeper";
    public KVPutGetGenerator generator = new KVPutGetGenerator(50,0);
    public ECSClient ecsClient;
    public Process zkProcess;

    private void deleteDirectoryAndContent(File path){
        File[] contents = path.listFiles();
        if(contents!=null){
            for(File f: contents){
                deleteDirectoryAndContent(f);
            }
        }
        path.delete();
    }

    @Override
    public void setUp() throws Exception {
        zkProcess = new ProcessBuilder().inheritIO().command("./zookeeper-3.4.11/bin/zkServer.sh","start-foreground").start();
        Thread.sleep(1000);
        ecsClient = new ECSClient(ECS_CONFIG_PATH,ZK_HOST_NAME,ZK_PORT_NUMBER);
        ecsClient.setDeployedServerJarPath("./code/ECE419_UT/m2-server.jar");
    }

    @Override
    public void tearDown() throws Exception {
        ecsClient.clearAllStorage();
        ecsClient.shutdown();
        Thread.sleep(2000);
        deleteDirectoryAndContent(new File(ZK_DATA_DIR));
        zkProcess.destroyForcibly();
    }

    @Test
    public void testBasic_ServerSetup() throws Exception {
        Map<String, KVServer> nodes = new HashMap<>();
        Collection<IECSNode> nodesC = ecsClient.setupNodes(2, "LFU", 5);
        for(IECSNode node : nodesC) {
            KVServer server = new KVServer(node.getNodeName(), ZK_HOST_NAME,ZK_PORT_NUMBER);
            nodes.put(node.getNodeName(), server);
        }
        Assert.assertEquals(true,ecsClient.awaitNodes(2,15*1000));
        Assert.assertEquals(true,ecsClient.start());
    }

    @Test
    public void testBasic_ServerSetup_SSH() throws Exception {
        ecsClient.addNodes(2,"LFU",5);
        Assert.assertEquals(true,ecsClient.start());
    }

    public void testBasic_Communication_SingleClientSingleServer() throws Exception{
        Collection<IECSNode> nodes = ecsClient.addNodes(1,"LFU",5);
        Assert.assertEquals(true,ecsClient.start());

        KVClient client = new KVClient();
        IECSNode firstNode = nodes.iterator().next();
        client.newConnection(firstNode.getNodeHost(),firstNode.getNodePort());
        client.setLogLevel(eKVLogLevel.OFF,eKVLogLevel.OFF);
        assertEquals(true, client.isConnected());

        for(int i = 0; i < 10; i++){
            KVCommand cmd = generator.getNextCommand();
            if(cmd.getCommandType()== KVCommandPattern.KVCommandType.GET){
                KVJSONMessage keyPair = client.executeCommand(cmd);
                assertEquals(true,generator.verify(keyPair.getKey(),keyPair.getValue()));
            }
            if(cmd.getCommandType() == KVCommandPattern.KVCommandType.PUT){
                KVJSONMessage response = client.executeCommand(cmd);
                assertEquals(KVMessage.StatusType.PUT_SUCCESS,response.getStatus());
            }
        }
    }
    public void testBasic_Communication_SingleClientMultiServer() throws Exception{
        Collection<IECSNode> nodes = ecsClient.addNodes(4,"LFU",5);
        Assert.assertEquals(true,ecsClient.start());

        KVClient client = new KVClient();
        IECSNode firstNode = nodes.iterator().next();
        client.newConnection(firstNode.getNodeHost(),firstNode.getNodePort());
        client.setLogLevel(eKVLogLevel.OFF,eKVLogLevel.OFF);
        assertEquals(true, client.isConnected());

        for(int i = 0; i < 100; i++){
            KVCommand cmd = generator.getNextCommand();
            if(cmd.getCommandType()== KVCommandPattern.KVCommandType.GET){
                KVJSONMessage keyPair = client.executeCommand(cmd);
                System.out.printf("Request %s, response %s\n",cmd.getValue("Key"),new String(keyPair.toBytes()));
                assertEquals(true,generator.verify(keyPair.getKey(),keyPair.getValue()));
            }
            if(cmd.getCommandType() == KVCommandPattern.KVCommandType.PUT){
                KVJSONMessage response = client.executeCommand(cmd);
                assertEquals(KVMessage.StatusType.PUT_SUCCESS,response.getStatus());
            }
        }
    }

    public void testBasic_Communication_RemoveServer() throws Exception{
        Collection<IECSNode> nodes = ecsClient.addNodes(3,"LFU",5);
        Assert.assertEquals(true,ecsClient.start());

        KVClient client = new KVClient();
        IECSNode firstNode = nodes.iterator().next();
        client.newConnection(firstNode.getNodeHost(),firstNode.getNodePort());
        client.setLogLevel(eKVLogLevel.OFF,eKVLogLevel.OFF);
        assertEquals(true, client.isConnected());

        int removedNodes = 1;
        int skipNode = 1;
        Iterator<IECSNode> itor = nodes.iterator();
        while (removedNodes > 0){
            if(skipNode > 0){
                skipNode--;
                itor.next();
            }
            else {
                assertTrue(ecsClient.removeNode(itor.next().getNodeName()));
                removedNodes -= 1;
            }
        }

        for(int i = 0; i < 100; i++){
            KVCommand cmd = generator.getNextCommand();
            if(cmd.getCommandType()== KVCommandPattern.KVCommandType.GET){
                KVJSONMessage keyPair = client.executeCommand(cmd);
                assertEquals(true,generator.verify(keyPair.getKey(),keyPair.getValue()));
            }
            if(cmd.getCommandType() == KVCommandPattern.KVCommandType.PUT){
                KVJSONMessage response = client.executeCommand(cmd);
                assertEquals(KVMessage.StatusType.PUT_SUCCESS,response.getStatus());
            }
        }
    }
}
