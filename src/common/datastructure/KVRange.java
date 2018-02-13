package common.datastructure;

public class KVRange<T extends Comparable> {
    private T lowerBound;
    private T upperBound;
    private boolean lowerInclusive;
    private boolean upperInclusive;

    public KVRange(T lowerbound, T upperbound, boolean lowerInclusive, boolean upperInclusive){
        this.lowerBound = lowerbound;
        this.upperBound = upperbound;
        this.lowerInclusive = lowerInclusive;
        this.upperInclusive = upperInclusive;
    }
    public T getLowerBound(){
        return lowerBound;
    }
    public T getUpperBound() {
        return upperBound;
    }
    public boolean inRange(T in){
        boolean inLowerBound;
        if(lowerInclusive) {
            inLowerBound = in.compareTo(lowerBound) >=0;
        }
        else{
            inLowerBound = in.compareTo(lowerBound) >0;
        }
        boolean inUpperBound;
        if(upperInclusive) {
            inUpperBound = in.compareTo(upperBound) <=0;
        }
        else{
            inUpperBound = in.compareTo(upperBound) <0;
        }
        if(lowerBound.compareTo(upperBound) <= 0){
            return inLowerBound && inUpperBound;
        }else {
            // lower bound is greater than the upper bound, enable wrapper
            return inLowerBound || inUpperBound;
        }
    }
}
