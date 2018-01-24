package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import testing.CommunicationTests.KVJSONMessageTest;
import testing.DatabaseTests.KVFIFOCacheTest;
import testing.DatabaseTests.KVLRUCacheTest;
import testing.DatabaseTests.MMStorageTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        KVFIFOCacheTest.class,
        KVLRUCacheTest.class,
        MMStorageTest.class
})

public class DatabaseTestSuites {
}