package testing;

import common.metadata.KVMetadata;
import junit.framework.Test;
import junit.framework.TestSuite;
import testing.ClientTests.KVCommandPatternTest;
import testing.CommunicationTests.*;
import testing.DatabaseTests.KVFIFOCacheTest;
import testing.DatabaseTests.KVLRUCacheTest;
import testing.DatabaseTests.KVLFUCacheTest;
import testing.IntegrationTests.*;
import testing.ServerTests.KVServerTest;
import testing.PerformanceTest.PerformanceEvaluation_PutGet.*;


public class AllTests {
	public static Test suite() {
		TestSuite clientSuite = new TestSuite("Basic Storage ServerTest-Suite");
		clientSuite.addTestSuite(ConnectionTest.class);
		clientSuite.addTestSuite(InteractionTest.class);
		clientSuite.addTestSuite(AdditionalTest.class);
		clientSuite.addTestSuite(KVJSONMessageTest.class);
		//clientSuite.addTestSuite(KVServerTest.class);
		clientSuite.addTestSuite(FunctionalityLRUServerTest.class);
		clientSuite.addTestSuite(FunctionalityFIFOServerTest.class);
		clientSuite.addTestSuite(FunctionalityLFUServerTest.class);
		clientSuite.addTestSuite(PersistencyFIFOServerTest.class);
		clientSuite.addTestSuite(PersistencyLRUServerTest.class);
		clientSuite.addTestSuite(PersistencyLFUServerTest.class);
		clientSuite.addTestSuite(KVCommandPatternTest.class);
		clientSuite.addTestSuite(KVLRUCacheTest.class);
		clientSuite.addTestSuite(KVFIFOCacheTest.class);
		clientSuite.addTestSuite(KVLFUCacheTest.class);
		clientSuite.addTestSuite(InteractionTest.class);
		clientSuite.addTestSuite(KVMetadataControllerTests.class);
		clientSuite.addTestSuite(KVMetadataTest.class);
		// clientSuite.addTestSuite(PutGet_FIFO_2080.class);
		// clientSuite.addTestSuite(PutGet_FIFO_5050.class);
		// clientSuite.addTestSuite(PutGet_FIFO_8020.class);
		// clientSuite.addTestSuite(PutGet_LRU_2080.class);
		// clientSuite.addTestSuite(PutGet_LRU_5050.class);
		// clientSuite.addTestSuite(PutGet_LRU_8020.class);
		// clientSuite.addTestSuite(PutGet_LFU_2080.class);
		// clientSuite.addTestSuite(PutGet_LFU_5050.class);
		// clientSuite.addTestSuite(PutGet_LFU_8020.class);
		return clientSuite;
	}
	
}
