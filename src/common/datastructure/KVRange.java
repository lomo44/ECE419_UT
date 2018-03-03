package common.datastructure;

import java.math.BigInteger;

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

    public static KVRange<BigInteger> fromString(
            String lowerBound, String upperBound, boolean LowerInclusive, boolean UpperInclusive){
        return new KVRange<>(new BigInteger(lowerBound),new BigInteger(upperBound),LowerInclusive,UpperInclusive);
    }
}
