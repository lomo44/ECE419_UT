package testing.PerformanceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import testing.PerformanceTest.PerformanceEvaluation_PutGet.*;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        PutGet_FIFO_2080.class,
        PutGet_FIFO_5050.class,
        PutGet_FIFO_8020.class,
        PutGet_LFU_2080.class,
        PutGet_LFU_5050.class,
        PutGet_LFU_8020.class,
        PutGet_LRU_2080.class,
        PutGet_LRU_5050.class,
        PutGet_LRU_8020.class
})

public class PerformanceEvaluationTestsuites {
}