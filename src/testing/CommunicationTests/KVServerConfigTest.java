package testing.CommunicationTests;

import static org.junit.Assert.*;

import junit.framework.TestCase;
import org.junit.Test;

import app_kvServer.KVServerConfig;
import common.messages.KVJSONMessage;

public class KVServerConfigTest extends TestCase {

	@Test
	public void testKVServerConfig_Serialization() {
		KVServerConfig serverconfigin = new KVServerConfig();
		serverconfigin.setKeyCacheStratagy("FIFO");
		serverconfigin.setCacheSize(5);
		byte[] deserialized = serverconfigin.toKVJSONMessage().toBytes();
		KVJSONMessage serialized = new KVJSONMessage();
		serialized.fromBytes(deserialized,0,deserialized.length);
		KVServerConfig serverConfigout = KVServerConfig.fromKVJSONMessage(serialized);
        assertEquals(serverconfigin.getCacheSize(),serverConfigout.getCacheSize());
        assertEquals(serverconfigin.getCacheStratagy(),serverConfigout.getCacheStratagy());
	}

}
