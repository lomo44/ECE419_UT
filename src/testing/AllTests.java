package testing;

import junit.framework.Test;
import junit.framework.TestSuite;
import testing.ClientTests.KVClientCommandPatternTest;
import testing.ClientTests.KVECSCommandPatternTest;
import testing.CommonModuleTests.KVRangeTest;
import testing.CommunicationTests.*;
import testing.DatabaseTests.*;
import testing.IntegrationTests.*;


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
		clientSuite.addTestSuite(KVClientCommandPatternTest.class);
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
		clientSuite.addTestSuite(KVECSCommandPatternTest.class);
		return clientSuite;
	}
	
}
