package testing.PerformanceTest.PerformanceEvaluation_PutGet;

public class PutGet_LRU_5050 extends PutGetEvaluation{
    @Override
    protected String setCacheType() {
        return "LRU";
    }

    @Override
    protected int setPutPercentage() {
        return 50;
    }
}