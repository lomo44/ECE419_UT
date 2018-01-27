package testing.PerformanceTest.PerformanceEvaluation_PutGet;

public class PutGet_LFU_2080 extends PutGetEvaluation{
    @Override
    protected String setCacheType() {
        return "LFU";
    }

    @Override
    protected int setPutPercentage() {
        return 20;
    }
}