package testing;

import common.messages.KVMigrationMessage;
import common.metadata.KVMetadata;
import database.storage.KVTablet;
import database.storage.KVTabletStorage;
import junit.framework.Test;
import junit.framework.TestSuite;
import testing.ClientTests.KVCommandPatternTest;
import testing.CommonModuleTests.KVRangeTest;
import testing.CommunicationTests.*;
import testing.DatabaseTests.*;
import testing.IntegrationTests.*;
import testing.ServerTests.KVServerTest;
import testing.PerformanceTest.PerformanceEvaluation_PutGet.*;


public class AllTests {
	public static Test suite() {
		TestSuite clientSuite = new TestSuite("Basic Storage ServerTest-Suite");
		//clientSuite.addTestSuite(ConnectionTest.class);
		//clientSuite.addTestSuite(InteractionTest.class);
		clientSuite.addTestSuite(LargeFileTranmissionTest.class);
		clientSuite.addTestSuite(AdditionalTest.class);
		clientSuite.addTestSuite(KVJSONMessageTest.class);
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
		clientSuite.addTestSuite(KVRangeTest.class);
		clientSuite.addTestSuite(KVTabletTest.class);
		clientSuite.addTestSuite(KVTabletStorageTest.class);
		clientSuite.addTestSuite(KVMigrationMessageTest.class);
		clientSuite.addTestSuite(KVMigrationTest.class);
		clientSuite.addTestSuite(KVServerConfigTest.class);
		return clientSuite;
	}
	
}
