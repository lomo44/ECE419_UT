package testing.CommonModuleTests;

import common.datastructure.KVRange;
import common.networknode.KVStorageNode;
import database.storage.KVStorage;
import junit.framework.TestCase;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;

public class KVStorageNodeTest extends TestCase{
    @Test
    public void testKVStorageNode_JSON(){
        KVStorageNode nodeA = new KVStorageNode("123",456,"789");
        nodeA.setHashRange(new KVRange<BigInteger>(BigInteger.valueOf(1),BigInteger.valueOf(5),true,false));
        KVStorageNode nodeB = KVStorageNode.fromJSONObject(nodeA.toJSONObject());
        assertEquals(nodeA.getHostName(),nodeB.getHostName());
        assertEquals(nodeA.getPortNumber(),nodeB.getPortNumber());
        assertEquals(nodeA.getUID(),nodeB.getUID());
        assertEquals(nodeA.getHashRange(),nodeB.getHashRange());
    }

    @Test
    public void testKVStorageNode_CollectionEqual(){
        KVStorageNode nodeA = new KVStorageNode("123",456,"789");
        KVStorageNode nodeB = new KVStorageNode("123",456,"789");
        assertEquals(nodeA,nodeB);
    }
}
