package testing.CommunicationTests;

import common.KVNetworkID;
import common.metadata.KVMetadata;
import junit.framework.TestCase;
import org.junit.Test;

public class KVMetadataTest extends TestCase {
    @Test
    public void testNetworkID_AddGet(){
        String hash = "aa";
        KVNetworkID id = new KVNetworkID("123",1);
        KVMetadata data = new KVMetadata();
        data.addNetworkIDHashPair(hash,id);
        KVNetworkID retid = data.getNetworkIDFromHash(hash);
        assertEquals(id,retid);
    }
    @Test
    public void testNetworkID_Merge(){
        String hashA = "aa";
        KVNetworkID idA = new KVNetworkID("123",1);
        KVMetadata dataA = new KVMetadata();
        dataA.addNetworkIDHashPair(hashA,idA);

        String hashB = "bb";
        KVNetworkID idB = new KVNetworkID("567",2);
        KVMetadata dataB = new KVMetadata();
        dataB.addNetworkIDHashPair(hashB,idB);

        assertEquals(true,dataA.merge(dataB));
        assertEquals(idB,dataA.getNetworkIDFromHash(hashB));
    }
    @Test
    public void testNetworkID_Merge_Same(){
        String hashA = "aa";
        KVNetworkID idA = new KVNetworkID("123",1);
        KVMetadata dataA = new KVMetadata();
        dataA.addNetworkIDHashPair(hashA,idA);

        String hashB = "aa";
        KVNetworkID idB = new KVNetworkID("123",1);
        KVMetadata dataB = new KVMetadata();
        dataB.addNetworkIDHashPair(hashB,idB);

        assertEquals(false,dataA.merge(dataB));
        assertEquals(idA,dataA.getNetworkIDFromHash(hashB));
    }

    @Test
    public void testNetworkID_toKVJSONMessage(){
        String hashA = "aa";
        KVNetworkID idA = new KVNetworkID("123",1);
        KVMetadata dataA = new KVMetadata();
        dataA.addNetworkIDHashPair(hashA,idA);
        KVMetadata dataB = KVMetadata.fromKVJSONMessage(dataA.toKVJSONMessage());
        assertEquals(dataA,dataB);
    }
}
