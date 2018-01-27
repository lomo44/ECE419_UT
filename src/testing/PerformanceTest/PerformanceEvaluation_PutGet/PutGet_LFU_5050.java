package testing.PerformanceTest.PerformanceEvaluation_PutGet;

public class PutGet_LFU_5050 extends PutGetEvaluation{
    @Override
    protected String setCacheType() {
        return "LFU";
    }

    @Override
    protected int setPutPercentage() {
        return 50;
    }
}