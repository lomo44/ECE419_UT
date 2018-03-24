package testing.ECSClientTests;

import app_kvECS.ECSClient;
import app_kvServer.KVServer;
import ecs.IECSNode;
import org.junit.Test;
import testing.ZookeeperTestBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FunctionalityTest extends ZookeeperTestBase{
    ECSClient ecsClient;
    public static final String zkHostname = "localhost";
    public static final int zkPort = 2181;
    public static final String configFile = "./ecs.config";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.ecsClient = new ECSClient(this.zkHostname, this.zkPort, configFile);
    }

    @Test
    public void testFunctionality_basic(){
        Map<String, KVServer> nodes = new HashMap<>();
        try {
            Collection<IECSNode> nodesC = ecsClient.setupNodes(2, "LFU", 5);
            for(IECSNode node : nodesC) {
                KVServer server = new KVServer(node.getNodeName(), this.zkHostname, this.zkPort);
                nodes.put(node.getNodeName(), server);
            }
            //ecsClient.createCluster();
            ecsClient.awaitNodes(2, 1000 * 15);

            ecsClient.start();

            ecsClient.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
