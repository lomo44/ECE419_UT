package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import testing.CommunicationTests.KVJSONMessageTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        KVJSONMessageTest.class,
})

public class CommunicationTestSuites {
}