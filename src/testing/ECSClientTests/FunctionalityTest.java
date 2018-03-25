package testing.ECSClientTests;

import app_kvClient.KVClient;
import app_kvECS.ECSClient;
import app_kvServer.KVServer;
import common.command.KVCommand;
import common.messages.KVJSONMessage;
import ecs.IECSNode;
import org.junit.Test;
import testing.ZookeeperTestBase;
import utility.KVPutGetGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
        this.ecsClient = new ECSClient(this.zkHostname, this.zkPort, configFile);
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
    }

    @Test
    public void testFunctionality_PutGet() throws Exception {
        KVClient client = new KVClient();
        KVServer server = (KVServer) nodes.values().toArray()[ThreadLocalRandom.current().nextInt(0,nodes.size())];
        assertTrue(!server.isStopped());
        client.newConnection(server.getHostname(),server.getPort());
        assertEquals(true,client.isConnected());
        for(int i = 0; i < 20; i++){
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
