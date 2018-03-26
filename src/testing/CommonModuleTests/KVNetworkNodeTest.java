package testing.CommonModuleTests;

import common.networknode.KVNetworkNode;
import junit.framework.TestCase;
import org.junit.Test;

public class KVNetworkNodeTest extends TestCase {
    @Test
    public void testKVNetworkNodeTest_JSON(){
        KVNetworkNode nodeA = new KVNetworkNode("123",456,"789");
        KVNetworkNode nodeB = KVNetworkNode.fromJSONObject(nodeA.toJSONObject());
        assertEquals(nodeA.getHostName(),nodeB.getHostName());
        assertEquals(nodeA.getUID(),nodeB.getUID());
        assertEquals(nodeA.getPortNumber(),nodeB.getPortNumber());
    }
}
