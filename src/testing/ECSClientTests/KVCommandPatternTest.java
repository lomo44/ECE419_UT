package testing.ECSClientTests;

import app_kvECS.CommandPatterns.*;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import junit.framework.TestCase;
import org.junit.Test;

public class KVCommandPatternTest extends TestCase {
    @Test
    public void testCommandPattern_AddNode_Match() {
        KVCommandPatternAddNode addNode = new KVCommandPatternAddNode();
        assertTrue(addNode.isMatched("addNode LRU 123"));
    }
    @Test
    public void testCommandPattern_AddNode_Valid() {
        String inputString = "addNode LRU 123";
        KVCommandPatternAddNode addNode = new KVCommandPatternAddNode();
        assertTrue(addNode.isMatched(inputString));
        KVCommand newCommand = addNode.generateCommand(inputString);
        assertEquals(newCommand.getValue("CacheStrategy"),"LRU");
        assertEquals(newCommand.getValue("CacheSize"),"123");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.ADD_NODE);
    }
    @Test
    public void testCommandPattern_AddNode_Invalid_CacheSize() {
        String inputString = "addNode LRU bbb";
        KVCommandPatternAddNode addNode = new KVCommandPatternAddNode();
        assertFalse(addNode.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_AddNode_Invalid_CacheStrategy() {
        String inputString = "addNode 123 123";
        KVCommandPatternAddNode addNode = new KVCommandPatternAddNode();
        assertFalse(addNode.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_AddNodes_Match() {
        KVCommandPatternAddNodes addNodes = new KVCommandPatternAddNodes();
        assertTrue(addNodes.isMatched("addNodes 10 LRU 123"));
    }
    @Test
    public void testCommandPattern_AddNodes_Valid() {
        String inputString = "addNodes 10 LRU 123";
        KVCommandPatternAddNodes addNodes = new KVCommandPatternAddNodes();
        assertTrue(addNodes.isMatched(inputString));
        KVCommand newCommand = addNodes.generateCommand(inputString);
        assertEquals(newCommand.getValue("NumNodes"),"10");
        assertEquals(newCommand.getValue("CacheStrategy"),"LRU");
        assertEquals(newCommand.getValue("CacheSize"),"123");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.ADD_NODES);
    }
    @Test
    public void testCommandPattern_AddNodes_Invalid_NumNodes() {
        String inputString = "addNodes bbb LRU 123";
        KVCommandPatternAddNodes addNodes = new KVCommandPatternAddNodes();
        assertFalse(addNodes.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_GetNodeByKey_Match() {
        KVCommandPatternGetNodeByKey getNodeByKey = new KVCommandPatternGetNodeByKey();
        assertTrue(getNodeByKey.isMatched("getNodeByKey abc"));
    }
    @Test
    public void testCommandPattern_GetNodeByKey_Invalid_Key() {
        String inputString = "getNodeByKey ";
        KVCommandPatternGetNodeByKey getNodeByKey = new KVCommandPatternGetNodeByKey();
        assertFalse(getNodeByKey.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_GetNodes_Match() {
        KVCommandPatternGetNodes getNodes = new KVCommandPatternGetNodes();
        assertTrue(getNodes.isMatched("getNodes"));
    }
    @Test
    public void testCommandPattern_GetNodes_Invalid() {
        String inputString = "getNodes abc";
        KVCommandPatternGetNodes getNodes = new KVCommandPatternGetNodes();
        assertFalse(getNodes.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_RemoveNode_Match() {
        KVCommandPatternRemoveNodeByIndex removeNode = new KVCommandPatternRemoveNodeByIndex();
        assertTrue(removeNode.isMatched("removeNode abc"));
    }
    @Test
    public void testCommandPattern_RemoveNode_Invalid_Key() {
        String inputString = "removeNode ";
        KVCommandPatternRemoveNodeByIndex removeNode = new KVCommandPatternRemoveNodeByIndex();
        assertFalse(removeNode.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_Shutdown_Match() {
        KVCommandPatternShutdown shutdown = new KVCommandPatternShutdown();
        assertTrue(shutdown.isMatched("shutdown"));
    }
    @Test
    public void testCommandPattern_Shutdown_Invalid() {
        String inputString = "qqshutdownqq";
        KVCommandPatternShutdown shutdown = new KVCommandPatternShutdown();
        assertFalse(shutdown.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_Start_Match() {
        KVCommandPatternStart start = new KVCommandPatternStart();
        assertTrue(start.isMatched("start"));
    }
    @Test
    public void testCommandPattern_Start_Invalid() {
        String inputString = "qqstartqq";
        KVCommandPatternStart start = new KVCommandPatternStart();
        assertFalse(start.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_Stop_Match() {
        KVCommandPatternStop stop = new KVCommandPatternStop();
        assertTrue(stop.isMatched("stop"));
    }
    @Test
    public void testCommandPattern_Stop_Invalid() {
        String inputString = "qqstopqq";
        KVCommandPatternStop stop = new KVCommandPatternStop();
        assertFalse(stop.isMatched(inputString));
    }

}
