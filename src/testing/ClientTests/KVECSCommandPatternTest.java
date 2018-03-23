package testing.ClientTests;

import app_kvECS.Commands.*;
import app_kvECS.ECSClientCommandLineParser;
import common.command.KVCommand;
import common.enums.eKVClusterOperationType;
import junit.framework.TestCase;
import org.junit.Test;

public class KVECSCommandPatternTest extends TestCase{
    private ECSClientCommandLineParser parser = new ECSClientCommandLineParser();

    @Test
    public void testKVCommandPatternAddNode_Invalid_Port(){
        KVCommand command = parser.getParsedCommand("addNode a b c");
        assertNull(command);
    }

    @Test
    public void testKVCommandPatternAddNode_Invalid_CacheStrategy(){
        KVCommand command = parser.getParsedCommand("addNode 5452 123 AAA");
        assertNull(command);
    }

    @Test
    public void testKVCommandPatternAddNode_Valid(){
        KVCommand command = parser.getParsedCommand("addNode 5452 12 FIFO");
        assertNotNull(command);
        KVCommandAddNode addNode = (KVCommandAddNode) command;
        assertEquals(5452,addNode.getPortNumber());
        assertEquals(12,addNode.getCacheSize());
        assertEquals("FIFO",addNode.getCacheStrategy());
    }

    @Test
    public void testKVCommandPatternAddNodes_Invalid_CacheStratagy(){
        KVCommand command = parser.getParsedCommand("addNodes 2 12 VVVV");
        assertNull(command);
    }

    @Test
    public void testKVCommandPatternAddNodes_Valid(){
        KVCommand command = parser.getParsedCommand("addNodes 2 12 FIFO");
        assertNotNull(command);
        KVCommandAddNodes addNodes = (KVCommandAddNodes) command;
        assertEquals(2,addNodes.getNumNodes());
        assertEquals(12,addNodes.getCacheSize());
        assertEquals("FIFO",addNodes.getCacheStrategy());
    }

    @Test
    public void testKVCommandPatternGetNodeByKey_Valid(){
        KVCommand command = parser.getParsedCommand("getNodeByKey 123");
        assertNotNull(command);
        KVCommandGetNodeByKey getNodeByKey = (KVCommandGetNodeByKey) command;
        assertEquals("123",getNodeByKey.getKey());
    }

    @Test
    public void testKVCommandPatternGetNodes_Valid(){
        KVCommand command = parser.getParsedCommand("getNodes");
        assertNotNull(command);
    }

    @Test
    public void testKVCommandPatternRemoveNode_Valid(){
        KVCommand command = parser.getParsedCommand("removeNode 1");
        assertNotNull(command);
        KVCommandRemoveNodeByIndex removeNode = (KVCommandRemoveNodeByIndex) command;
        assertEquals(1,removeNode.getIndex());
    }

    @Test
    public void testKVCommandPatternRemoveNodeByName(){
        KVCommand command = parser.getParsedCommand("removeNode a");
        assertNotNull(command);
        KVCommandRemoveNodeByName cmd = (KVCommandRemoveNodeByName) command;
        assertEquals("a",cmd.getNodeName());
    }

    @Test
    public void testKVCommandPattern_ShutDown(){
        KVCommand command = parser.getParsedCommand("shutDown");
        assertNotNull(command);
    }

    @Test
    public void testKVCommandPattern_Start(){
        KVCommand command = parser.getParsedCommand("start");
        assertNotNull(command);
    }

    @Test
    public void testKVCommandPattern_Stop(){
        KVCommand command = parser.getParsedCommand("stop");
        assertNotNull(command);
    }

    @Test
    public void testKVCommandJoinCluster(){
        KVCommand command = parser.getParsedCommand("joinCluster node1 cluster1");
        KVCommandModifyClusterNode realCmd = (KVCommandModifyClusterNode)command;
        assertNotNull(realCmd);
        assertEquals("node1",realCmd.getNodeName());
        assertEquals("cluster1",realCmd.getClusterName());
        assertEquals(eKVClusterOperationType.JOIN,realCmd.getClusterOperationType());
    }

    @Test
    public void testKVCommandExitCluster(){
        KVCommand command = parser.getParsedCommand("leaveCluster node1 cluster1");
        KVCommandModifyClusterNode realCmd = (KVCommandModifyClusterNode)command;
        assertNotNull(realCmd);
        assertEquals("node1",realCmd.getNodeName());
        assertEquals("cluster1",realCmd.getClusterName());
        assertEquals(eKVClusterOperationType.EXIT,realCmd.getClusterOperationType());
    }

    @Test
    public void testKVCommandcreateCluster(){
        KVCommand command = parser.getParsedCommand("createCluster cluster1");
        KVCommandModifyCluster realCmd = (KVCommandModifyCluster)command;
        assertNotNull(realCmd);
        assertEquals("cluster1",realCmd.getClusterName());
        assertEquals(eKVClusterOperationType.CREATE,realCmd.getClusterOperationType());
    }

    @Test
    public void testKVCommandremoveCluster(){
        KVCommand command = parser.getParsedCommand("removeCluster cluster1");
        KVCommandModifyCluster realCmd = (KVCommandModifyCluster)command;
        assertNotNull(realCmd);
        assertEquals("cluster1",realCmd.getClusterName());
        assertEquals(eKVClusterOperationType.REMOVE,realCmd.getClusterOperationType());
    }
}
