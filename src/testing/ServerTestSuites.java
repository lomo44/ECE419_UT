package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        KVJSONMessageTest.class,
        KVServerTest.class,
})

public class ServerTestSuites {
}