package testing.CommunicationTests;

import common.networknode.KVNetworkNode;
import common.metadata.KVMetadata;
import common.networknode.KVStorageNode;
import junit.framework.TestCase;
import org.junit.Test;

import java.math.BigInteger;

public class KVMetadataTest extends TestCase {
    @Test
    public void testNetworkID_AddGet(){
        BigInteger hash = BigInteger.valueOf(123);
        KVStorageNode id = new KVStorageNode("123",1,"temp");
        KVMetadata data = new KVMetadata();
        data.addStorageNodeHashPair(hash,id);
        KVStorageNode retid = data.getStorageNodeFromHash(hash);
        assertEquals(id,retid);
    }
    @Test
    public void testNetworkID_Merge(){
        BigInteger hashA = BigInteger.valueOf(123);
        KVStorageNode idA = new KVStorageNode("123",1,"temp1");
        KVMetadata dataA = new KVMetadata();
        dataA.addStorageNodeHashPair(hashA,idA);

        BigInteger hashB = BigInteger.valueOf(456);
        KVStorageNode idB = new KVStorageNode("567",2,"temp2");
        KVMetadata dataB = new KVMetadata();
        dataB.addStorageNodeHashPair(hashB,idB);

        assertEquals(true,dataA.merge(dataB));
        assertEquals(idB,dataA.getStorageNodeFromHash(hashB));
    }

    @Test
    public void testNetworkID_toKVJSONMessage(){
        BigInteger hashA = BigInteger.valueOf(123);
        KVStorageNode idA = new KVStorageNode("123",1,"temp4");
        KVMetadata dataA = new KVMetadata();
        dataA.addStorageNodeHashPair(hashA,idA);
        KVMetadata dataB = KVMetadata.fromKVJSONMessage(dataA.toKVJSONMessage());
        assertEquals(dataA,dataB);
    }
}
