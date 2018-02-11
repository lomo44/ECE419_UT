package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import testing.CommunicationTests.KVJSONMessageTest;
import testing.ServerTests.KVServerTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        KVJSONMessageTest.class,
        KVServerTest.class,
})

public class ServerTestSuites {
}