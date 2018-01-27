package testing.PerformanceTest.PerformanceEvaluation_PutGet;

public class PutGet_LRU_8020 extends PutGetEvaluation{
    @Override
    protected String setCacheType() {
        return "LRU";
    }

    @Override
    protected int setPutPercentage() {
        return 80;
    }
}