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

    @Override
    public boolean equals(Object o) {
        KVRange<T> a = (KVRange<T>) o;
        return a.upperBound.compareTo(this.upperBound) == 0 &&
                a.lowerBound.compareTo(this.lowerBound) == 0 &&
                a.upperInclusive == this.upperInclusive &&
                a.lowerInclusive == this.lowerInclusive;
    }

    public boolean isIntersect(KVRange<T> b){
        boolean left,right;
        if(this.lowerInclusive && b.upperInclusive){
            left = this.lowerBound.compareTo(b.upperBound) <=0;
        }
        else {
            left = this.lowerBound.compareTo(b.upperBound) < 0;
        }

        if(this.upperInclusive && b.lowerInclusive){
            right = b.lowerBound.compareTo(this.upperBound) <=0;
        }
        else {
            right = b.lowerBound.compareTo(this.upperBound) < 0;
        }
        return left && right;
    }

    public boolean isInclusive(KVRange<T> b){
        boolean lower,upper;
        if(this.lowerInclusive){
            lower = this.lowerBound.compareTo(b.lowerBound) <= 0;
        }
        else{
            lower = this.lowerBound.compareTo(b.lowerBound) < 0;
        }

        if(this.upperInclusive){
            upper = this.upperBound.compareTo(b.upperBound) >= 0;
        }
        else{
            upper = this.upperBound.compareTo(b.upperBound) > 0;
        }
        return lower&&upper;
    }

    public String[] getHashRange() {
        if (this.lowerInclusive) {
            return new String[] {upperBound.toString(),lowerBound.toString()};
        } else {
            return new String[] {lowerBound.toString(),upperBound.toString()};
        }
    }

    /**
     * Assume b is an extension of the current range, get the extended range
     * @param b
     * @return
     */
    public KVRange<T> getExtension(KVRange<T> b){
        T lowerBound;
        T upperBound;
        boolean upperInclusive;
        boolean lowerInclusive;
        if(this.upperBound.compareTo(b.upperBound) == 0 && this.upperInclusive == b.upperInclusive){
            // extended to the lower bound;
            // extended to the upper bound;
            if(this.lowerBound.compareTo(b.upperBound)<=0){
                lowerBound = this.lowerBound;
                lowerInclusive = this.lowerInclusive;
                upperBound = b.lowerBound;
                upperInclusive = !b.lowerInclusive;
            }
            else{
                lowerBound = b.lowerBound;
                lowerInclusive = b.lowerInclusive;
                upperBound = this.lowerBound;
                upperInclusive= !this.upperInclusive;
            }
            return new KVRange<>(lowerBound,upperBound,lowerInclusive,upperInclusive);
        }
        if(this.lowerBound.compareTo(b.lowerBound) == 0 && this.lowerInclusive== b.lowerInclusive){
            // extended to the upper bound;
            if(this.upperBound.compareTo(b.upperBound)<=0){
                lowerBound = this.upperBound;
                lowerInclusive = !this.upperInclusive;
                upperBound = b.upperBound;
                upperInclusive = b.upperInclusive;
            }
            else{
                lowerBound = b.upperBound;
                lowerInclusive = !b.upperInclusive;
                upperBound = this.upperBound;
                upperInclusive= this.upperInclusive;
            }
            return new KVRange<>(lowerBound,upperBound,lowerInclusive,upperInclusive);
        }
        return null;
    }
}
