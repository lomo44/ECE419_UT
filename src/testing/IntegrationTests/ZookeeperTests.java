package testing.IntegrationTests;


import app_kvECS.ECSClient;
import app_kvServer.KVServer;
import ecs.IECSNode;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ZookeeperTests extends TestCase{

    public static final String ECS_CONFIG_PATH = "./ecs.config";
    public static final String ZK_HOST_NAME = "localhost";
    public static final int ZK_PORT_NUMBER = 2181;
    public static final String ZK_DATA_DIR = "./tmp/zookeeper";

    @Override
    public void setUp() throws Exception {
        Runtime.getRuntime().exec("./zookeeper-3.4.11/bin/zkServer.sh start");
        Thread.sleep(1000);
    }

    @Override
    public void tearDown() throws Exception {
        Runtime.getRuntime().exec("./zookeeper-3.4.11/bin/zkServer.sh stop");
        //Runtime.getRuntime().exec("rm -rf "+ZK_DATA_DIR);
    }

    @Test
    public void testBasic_ServerSetup() throws Exception {
        ECSClient ecsClient = new ECSClient(ECS_CONFIG_PATH,ZK_HOST_NAME,ZK_PORT_NUMBER);
        Map<String, KVServer> nodes = new HashMap<>();
        Collection<IECSNode> nodesC = ecsClient.setupNodes(2, "LFU", 5);
        for(IECSNode node : nodesC) {
            KVServer server = new KVServer(node.getNodeName(), ZK_HOST_NAME,ZK_PORT_NUMBER);
            nodes.put(node.getNodeName(), server);
        }
        Assert.assertEquals(true,ecsClient.awaitNodes(2,15*1000));
        Assert.assertEquals(true,ecsClient.start());
        Assert.assertEquals(true,ecsClient.shutdown());
    }

    @Test
    public void testBasic_ServerSetup_SSH() throws Exception {
        ECSClient ecsClient = new ECSClient(ECS_CONFIG_PATH,ZK_HOST_NAME,ZK_PORT_NUMBER);
        ecsClient.addNodes(2,"LFU",5);
        Assert.assertEquals(true,ecsClient.start());
        Assert.assertEquals(true,ecsClient.shutdown());
        Thread.sleep(10000);
    }
}
