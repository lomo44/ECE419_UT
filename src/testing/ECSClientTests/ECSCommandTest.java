package testing.ECSClientTests;

import app_kvECS.Commands.KVCommandAddNodes;
import app_kvECS.Commands.KVCommandSetServerJarPath;
import app_kvECS.Commands.KVCommandStart;
import app_kvECS.ECSClient;
import common.messages.KVJSONMessage;
import junit.framework.TestCase;
import org.junit.Test;

import static common.enums.eKVExtendStatusType.ADD_NODE_SUCCESS;
import static common.enums.eKVExtendStatusType.START_SUCCESS;

public class ECSCommandTest extends TestCase {
    public static final String CONFIG_FILE_PATH = "./ecs.config";
    public static final String EXECUTABLE_PATH = "./code/ECE419_UT/m2-server.jar";
    ECSClient ecsClient = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ecsClient =  new ECSClient(CONFIG_FILE_PATH,"localhost",2181);
        KVCommandSetServerJarPath setPath = new KVCommandSetServerJarPath(EXECUTABLE_PATH);
        ecsClient.executeCommand(setPath);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(ecsClient!=null){
            ecsClient.stop();
            ecsClient.shutdown();
        }
    }
    @Test
    public void testECSCommand_AddNodesStartup_NoCluster(){
        KVCommandAddNodes addNodes = new KVCommandAddNodes();
        addNodes.setNumNodes(2);
        addNodes.setCacheSize(10);
        addNodes.setCacheStrategy("FIFO");
        addNodes.setClusterName("");
        KVJSONMessage ret = ecsClient.executeCommand(addNodes);
        assertEquals(ADD_NODE_SUCCESS, ret.getExtendStatusType());
        ret = ecsClient.executeCommand(new KVCommandStart());
        assertEquals(START_SUCCESS, ret.getExtendStatusType());
    }
    @Test
    public void testECSCommand_AddNodesStartup_Cluster(){
        KVCommandAddNodes addNodes = new KVCommandAddNodes();
        addNodes.setNumNodes(2);
        addNodes.setCacheSize(10);
        addNodes.setCacheStrategy("FIFO");
        addNodes.setClusterName("Cluster0");
        KVJSONMessage ret = ecsClient.executeCommand(addNodes);
        assertEquals(ADD_NODE_SUCCESS, ret.getExtendStatusType());
        ret = ecsClient.executeCommand(new KVCommandStart());
        assertEquals(START_SUCCESS, ret.getExtendStatusType());
    }
}
