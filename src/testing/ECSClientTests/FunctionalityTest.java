package testing.ECSClientTests;

import app_kvClient.KVClient;
import app_kvECS.ECSClient;
import app_kvServer.KVServer;
import app_kvServer.KVServerConfig;
import common.command.KVCommand;
import common.messages.KVJSONMessage;
import common.networknode.KVStorageNode;
import ecs.IECSNode;
import org.junit.Test;
import testing.ZookeeperTestBase;
import utility.KVPutGetGenerator;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static common.enums.eKVExtendStatusType.GET_SUCCESS;
import static common.enums.eKVExtendStatusType.PUT;
import static common.enums.eKVExtendStatusType.PUT_SUCCESS;

public class FunctionalityTest extends ZookeeperTestBase{
    ECSClient ecsClient;
    public static final String zkHostname = "localhost";
    public static final int zkPort = 2181;
    public static final String configFile = "./ecs.config";
    public Map<String, KVServer> nodes;
    public KVPutGetGenerator generator = new KVPutGetGenerator(50,0);
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.ecsClient = new ECSClient(configFile,this.zkHostname, this.zkPort);
        nodes = new HashMap<>();
        Collection<IECSNode> nodesC = ecsClient.setupNodes(2, "LFU", 5);
        for (IECSNode node : nodesC) {
            KVServer server = new KVServer(node.getNodeName(), this.zkHostname, this.zkPort);
            nodes.put(node.getNodeName(), server);
        }
        //ecsClient.createCluster();
        ecsClient.awaitNodes(2, 1000 * 15);

        ecsClient.start();
    }

    @Override
    protected void tearDown() throws Exception {
        ecsClient.stop();
        ecsClient.clearAllStorage();
        this.ecsClient.shutdown();
        Thread.sleep(3000);
    }

    @Test
    public void testFunctionality_PutGet() throws Exception {
        KVClient client = new KVClient();
        KVServer server = (KVServer) nodes.values().toArray()[ThreadLocalRandom.current().nextInt(0,nodes.size())];
        assertTrue(!server.isStopped());
        client.newConnection(server.getHostname(),server.getPort());
        assertEquals(true,client.isConnected());
        for(int i = 0; i < 50; i++){
            KVCommand command = generator.getNextCommand();
            KVJSONMessage ret = client.executeCommand(command);
            switch (command.getCommandType()){
                case GET:{
                    assertEquals(GET_SUCCESS,ret.getExtendStatusType());
                    assertTrue(generator.verify(ret.getKey(),ret.getValue()));
                    break;
                }
                case PUT:{
                    assertEquals(PUT_SUCCESS,ret.getExtendStatusType());
                    break;
                }
            }
        }
    }

    @Test
    public void testFunctionality_AddNodePutGet() throws Exception{
        // Create 1 more server
        List<KVStorageNode> selectedServer = ecsClient.selectServerToSetup(1);
        List<KVServerConfig> configs = new ArrayList<>();
        for(KVStorageNode node :selectedServer){
            configs.add(ecsClient.createServerConfig(node,"FIFO",10,"cluster0"));
        }
        ecsClient.setupNodes(selectedServer,configs);
        for (KVStorageNode node : selectedServer) {
            KVServer server = new KVServer(node.getUID(), this.zkHostname, this.zkPort);
            nodes.put(node.getUID(), server);
        }
        ecsClient.awaitNodes(3,15*1000);

        KVClient client = new KVClient();
        KVServer server = (KVServer) nodes.values().toArray()[ThreadLocalRandom.current().nextInt(0,nodes.size())];
        assertTrue(!server.isStopped());
        client.newConnection(server.getHostname(),server.getPort());
        assertEquals(true,client.isConnected());
        for(int i = 0; i < 50; i++){
            KVCommand command = generator.getNextCommand();
            KVJSONMessage ret = client.executeCommand(command);
            switch (command.getCommandType()){
                case GET:{
                    assertEquals(GET_SUCCESS,ret.getExtendStatusType());
                    assertTrue(generator.verify(ret.getKey(),ret.getValue()));
                    break;
                }
                case PUT:{
                    assertEquals(PUT_SUCCESS,ret.getExtendStatusType());
                    break;
                }
            }
        }
    }

}
