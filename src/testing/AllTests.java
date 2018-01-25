package testing;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.junit.ClassRule;
import org.junit.rules.Timeout;
import testing.ClientTests.KVCommandPatternTest;
import testing.CommunicationTests.ConnectionTest;
import testing.CommunicationTests.InteractionTest;
import testing.CommunicationTests.KVJSONMessageTest;
import testing.DatabaseTests.KVFIFOCacheTest;
import testing.DatabaseTests.KVLRUCacheTest;
import testing.IntegrationTests.FunctionalityFIFOServerTest;
import testing.IntegrationTests.FunctionalityLRUServerTest;
import testing.IntegrationTests.PersistencyFIFOServerTest;
import testing.IntegrationTests.PersistencyLRUServerTest;
import testing.ServerTests.KVServerTest;




public class AllTests {
	@ClassRule
	public static Timeout classTimeout = new Timeout(5000);
	public static Test suite() {
		TestSuite clientSuite = new TestSuite("Basic Storage ServerTest-Suite");
//		clientSuite.addTestSuite(ConnectionTest.class);
//		clientSuite.addTestSuite(InteractionTest.class);
//		clientSuite.addTestSuite(AdditionalTest.class);
//		clientSuite.addTestSuite(KVJSONMessageTest.class);
		clientSuite.addTestSuite(KVServerTest.class);
		clientSuite.addTestSuite(FunctionalityLRUServerTest.class);
		clientSuite.addTestSuite(FunctionalityFIFOServerTest.class);
//		clientSuite.addTestSuite(KVCommandPatternTest.class);
//		clientSuite.addTestSuite(KVLRUCacheTest.class);
//		clientSuite.addTestSuite(KVFIFOCacheTest.class);
//		clientSuite.addTestSuite(PersistencyFIFOServerTest.class);
//		clientSuite.addTestSuite(PersistencyLRUServerTest.class);
		return clientSuite;
	}
	
}
