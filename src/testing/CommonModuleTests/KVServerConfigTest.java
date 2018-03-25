package testing.CommonModuleTests;

import junit.framework.TestCase;
import org.junit.Test;

import app_kvServer.KVServerConfig;
import common.messages.KVJSONMessage;

import java.util.HashSet;

public class KVServerConfigTest extends TestCase {

	@Test
	public void testKVServerConfig_Serialization() {
		KVServerConfig serverconfigin = new KVServerConfig();
		serverconfigin.setCacheStratagy("FIFO");
		serverconfigin.setCacheSize(5);
		HashSet<String> clusterNeededToJoin = new HashSet<>();
		clusterNeededToJoin.add("cluster1");
		serverconfigin.setBelongedCluster(clusterNeededToJoin);
		byte[] deserialized = serverconfigin.toKVJSONMessage().toBytes();
		KVJSONMessage serialized = new KVJSONMessage();
		serialized.fromBytes(deserialized,0,deserialized.length);
		KVServerConfig serverConfigout = KVServerConfig.fromKVJSONMessage(serialized);
        assertEquals(serverconfigin.getCacheSize(),serverConfigout.getCacheSize());
        assertEquals(serverconfigin.getCacheStratagy(),serverConfigout.getCacheStratagy());
        assertEquals(serverconfigin.getBelongedCluster(),serverConfigout.getBelongedCluster());
	}

}
