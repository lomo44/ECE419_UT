package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import testing.ClientTests.KVCommandPatternTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        KVCommandPatternTest.class,
})

public class ClientTestSuites {
}