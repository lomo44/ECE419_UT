package testing;

import junit.framework.Test;
import junit.framework.TestSuite;
import testing.ClientTests.KVCommandPatternTest;
import testing.CommunicationTests.ConnectionTest;
import testing.CommunicationTests.InteractionTest;
import testing.CommunicationTests.KVJSONMessageTest;
import testing.DatabaseTests.KVFIFOCacheTest;
import testing.DatabaseTests.KVLRUCacheTest;
import testing.DatabaseTests.KVLFUCacheTest;
import testing.IntegrationTests.FunctionalityFIFOServerTest;
import testing.IntegrationTests.FunctionalityLRUServerTest;
import testing.ServerTests.KVServerTest;


public class AllTests {
	public static Test suite() {
		TestSuite clientSuite = new TestSuite("Basic Storage ServerTest-Suite");
		clientSuite.addTestSuite(ConnectionTest.class);
		clientSuite.addTestSuite(InteractionTest.class);
		clientSuite.addTestSuite(AdditionalTest.class);
		clientSuite.addTestSuite(KVJSONMessageTest.class);
		clientSuite.addTestSuite(KVServerTest.class);
		clientSuite.addTestSuite(FunctionalityLRUServerTest.class);
		clientSuite.addTestSuite(FunctionalityFIFOServerTest.class);
		clientSuite.addTestSuite(KVCommandPatternTest.class);
		clientSuite.addTestSuite(KVLRUCacheTest.class);
		clientSuite.addTestSuite(KVFIFOCacheTest.class);
		clientSuite.addTestSuite(KVLFUCacheTest.class);
		return clientSuite;
	}
	
}
