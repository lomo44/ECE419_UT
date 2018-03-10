package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import testing.ClientTests.KVClientCommandPatternTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        KVClientCommandPatternTest.class,
})

public class ClientTestSuites {
}