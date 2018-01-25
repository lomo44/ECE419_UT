package testing;

import java.io.IOException;

import database.cache.KVFIFOCache;
import database.cache.KVLRUCache;
import org.apache.log4j.Level;

import app_kvServer.KVServer;
import junit.framework.Test;
import junit.framework.TestSuite;
import logger.LogSetup;


public class AllTests {

	static {
		try {
			new LogSetup("logs/testing/test.log", Level.ERROR);
			new KVServer(50000, 10, "FIFO");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public static Test suite() {
		TestSuite clientSuite = new TestSuite("Basic Storage ServerTest-Suite");
		clientSuite.addTestSuite(ConnectionTest.class);
		clientSuite.addTestSuite(InteractionTest.class);
		clientSuite.addTestSuite(AdditionalTest.class);
		clientSuite.addTestSuite(KVJSONMessageTest.class);
		clientSuite.addTestSuite(KVServerTest.class);
		clientSuite.addTestSuite(KVClientTest.class);
		clientSuite.addTestSuite(KVCommandPatternTest.class);
		clientSuite.addTestSuite(KVLRUCacheTest.class);
		clientSuite.addTestSuite(KVFIFOCacheTest.class);
		clientSuite.addTestSuite(KVLFUCacheTest.class);
		return clientSuite;
	}
	
}
