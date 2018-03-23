package testing.CommonModuleTests;

import common.datastructure.KVRange;
import common.enums.eKVNetworkNodeType;
import common.networknode.KVNetworkNode;
import common.networknode.KVStorageCluster;
import common.networknode.KVStorageNode;
import database.storage.KVStorage;
import junit.framework.TestCase;
import org.junit.Test;

import java.math.BigInteger;

public class KVStorageClusterTest extends TestCase {
    @Test
    public void testKVStorageCluster_JSON(){
        KVStorageCluster clusterA = new KVStorageCluster("Level_1_c1");
        clusterA.setHashRange(new KVRange<>(BigInteger.valueOf(1),BigInteger.valueOf(5),true,false));
        KVStorageCluster subClusterB = new KVStorageCluster("Level_2_c1");
        subClusterB.setHashRange(new KVRange<>(BigInteger.valueOf(2),BigInteger.valueOf(9),false,false));
        clusterA.addNode(subClusterB);
        KVStorageCluster clusterC = KVStorageCluster.fromJSONObject(clusterA.toJSONObject());
        assertEquals(clusterA,clusterC);
        assertTrue(clusterC.getChildNodes().contains(subClusterB));
    }

    @Test
    public void testKVStorageCluster_JSON_Mixed(){
        KVStorageCluster clusterA = new KVStorageCluster("Level_1_c1");
        clusterA.setHashRange(new KVRange<>(BigInteger.valueOf(1),BigInteger.valueOf(5),true,false));
        KVStorageCluster subClusterB = new KVStorageCluster("Level_2_c1");
        KVStorageNode subNode = new KVStorageNode("level_2_Node",123,"456");
        clusterA.addNode(subNode);
        subClusterB.setHashRange(new KVRange<>(BigInteger.valueOf(2),BigInteger.valueOf(9),false,false));
        clusterA.addNode(subClusterB);
        KVStorageCluster clusterC = KVStorageCluster.fromJSONObject(clusterA.toJSONObject());
        assertEquals(clusterA,clusterC);
        assertTrue(clusterC.getChildNodes().contains(subClusterB));
    }

    @Test
    public void testKVStorageCluster_PartialSerialization_StorageNode(){
        KVStorageCluster clusterA = new KVStorageCluster("123_456");
        clusterA.setHashRange(new KVRange<>(BigInteger.valueOf(1),BigInteger.valueOf(5),true,false));
        clusterA.addNode(new KVStorageNode("123",5,"11111"));
        KVStorageNode nodeB = KVStorageNode.fromJSONObject(clusterA.toJSONObject());
        assertEquals(eKVNetworkNodeType.STORAGE_CLUSTER,nodeB.getNodeType());
    }

    @Test
    public void testKVStorageCluster_PartialSerialization_NetworkNode(){
        KVStorageCluster clusterA = new KVStorageCluster("123_456");
        clusterA.setHashRange(new KVRange<>(BigInteger.valueOf(1),BigInteger.valueOf(5),true,false));
        clusterA.addNode(new KVStorageNode("123",5,"11111"));
        KVNetworkNode nodeB = KVNetworkNode.fromJSONObject(clusterA.toJSONObject());
        assertEquals(eKVNetworkNodeType.STORAGE_CLUSTER,nodeB.getNodeType());
    }
}
