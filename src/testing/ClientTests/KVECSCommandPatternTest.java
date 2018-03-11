package testing.ClientTests;

import app_kvClient.Commands.KVCommandGet;
import app_kvECS.CommandPatterns.KVCommandPatternAddNode;
import app_kvECS.Commands.KVCommandAddNode;
import app_kvECS.Commands.KVCommandAddNodes;
import app_kvECS.Commands.KVCommandGetNodeByKey;
import app_kvECS.Commands.KVCommandRemoveNode;
import app_kvECS.ECSClientCommandLineParser;
import common.command.KVCommand;
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
        KVCommandRemoveNode removeNode = (KVCommandRemoveNode) command;
        assertEquals(1,removeNode.getIndex());
    }

    @Test
    public void testKVCommandPatternRemoveNode_Invalid(){
        KVCommand command = parser.getParsedCommand("removeNode a");
        assertNull(command);
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
}
