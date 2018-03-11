package testing.CommonModuleTests;

import common.datastructure.KVRange;
import junit.framework.TestCase;
import org.junit.Test;

public class KVRangeTest extends TestCase {
    @Test
    public void testKVRangeTest_BothExclusive(){
        KVRange<Integer> range = new KVRange<>(1,3,false,false);
        assertEquals(true,range.inRange(2));
        assertEquals(false,range.inRange(1));
        assertEquals(false,range.inRange(3));
    }
    @Test
    public void testKVRangeTest_BothInclusive(){
        KVRange<Integer> range = new KVRange<>(1,3,true,true);
        assertEquals(true,range.inRange(2));
        assertEquals(true,range.inRange(1));
        assertEquals(true,range.inRange(3));
    }
    @Test
    public void testKVRangeTest_LeftInclusive_RightExclusive(){
        KVRange<Integer> range = new KVRange<>(1,3,true,false);
        assertEquals(true,range.inRange(2));
        assertEquals(true,range.inRange(1));
        assertEquals(false,range.inRange(3));
    }
    @Test
    public void testKVRangeTest_LeftExclusive_RightInclusive(){
        KVRange<Integer> range = new KVRange<>(1,3,false,true);
        assertEquals(true,range.inRange(2));
        assertEquals(false,range.inRange(1));
        assertEquals(true,range.inRange(3));
    }
    @Test
    public void testKVRangeTest_EqualInclusive(){
        KVRange<Integer> range = new KVRange<>(1,1,true,true);
        assertEquals(false,range.inRange(2));
        assertEquals(true,range.inRange(1));
        assertEquals(false,range.inRange(3));
    }
    @Test
    public void testKVRangeTest_EqualExclusive(){
        KVRange<Integer> range = new KVRange<>(1,1,false,false);
        assertEquals(false,range.inRange(2));
        assertEquals(false,range.inRange(1));
        assertEquals(false,range.inRange(3));
    }
    @Test
    public void testKVRangeTest_Wrap_BothInclusive(){
        KVRange<Integer> range = new KVRange<>(4,2,true,true);
        assertEquals(true,range.inRange(2));
        assertEquals(true,range.inRange(1));
        assertEquals(false,range.inRange(3));
        assertEquals(true,range.inRange(4));
    }
    @Test
    public void testKVRangeTest_Wrap_BothExclusive(){
        KVRange<Integer> range = new KVRange<>(4,2,false,false);
        assertEquals(false,range.inRange(2));
        assertEquals(true,range.inRange(1));
        assertEquals(false,range.inRange(3));
        assertEquals(false,range.inRange(4));
    }
    @Test
    public void testKVRangeTest_Wrap_LeftExclusive_RightInclusive(){
        KVRange<Integer> range = new KVRange<>(4,2,false,true);
        assertEquals(true,range.inRange(2));
        assertEquals(true,range.inRange(1));
        assertEquals(false,range.inRange(3));
        assertEquals(false,range.inRange(4));
    }
    @Test
    public void testKVRangeTest_Wrap_LeftInclusive_RightExclusive(){
        KVRange<Integer> range = new KVRange<>(4,2,true,false);
        assertEquals(false,range.inRange(2));
        assertEquals(true,range.inRange(1));
        assertEquals(false,range.inRange(3));
        assertEquals(true,range.inRange(4));
    }

    @Test
    public void testKVRangeTest_ExtendRage_UpperInclusive(){
        KVRange<Integer> rangeA = new KVRange<>(4,5,true,true);
        KVRange<Integer> rangeB = new KVRange<>(4,10, true,true);
        KVRange<Integer> rangeC = rangeB.getExtension(rangeA);
        assertEquals(rangeC, new KVRange<>(5,10,false,true));
    }

    @Test
    public void testKVRangeTest_ExtendRage_UpperExclusive(){
        KVRange<Integer> rangeA = new KVRange<>(4,5,true,false);
        KVRange<Integer> rangeB = new KVRange<>(4,10, true,true);
        KVRange<Integer> rangeC = rangeB.getExtension(rangeA);
        assertEquals(rangeC, new KVRange<>(5,10,true,true));
    }

    @Test
    public void testKVRangeTest_ExtendRage_LowerInclusive(){
        KVRange<Integer> rangeA = new KVRange<>(4,5,true,true);
        KVRange<Integer> rangeB = new KVRange<>(0,5, true,true);
        KVRange<Integer> rangeC = rangeB.getExtension(rangeA);
        assertEquals(rangeC, new KVRange<>(0,4,true,false));
    }

    @Test
    public void testKVRangeTest_ExtendRage_LowerExclusive(){
        KVRange<Integer> rangeA = new KVRange<>(4,5,false,true);
        KVRange<Integer> rangeB = new KVRange<>(0,5, true,true);
        KVRange<Integer> rangeC = rangeB.getExtension(rangeA);
        assertEquals(rangeC, new KVRange<>(0,4,true,true));
    }

    @Test
    public void testKVRangeTest_ExtendRange_Wrapped(){
        KVRange<Integer> rangeA = new KVRange<>(9,6,true,true);
        KVRange<Integer> rangeB = new KVRange<>(9,8,true,true);
        KVRange<Integer> rangeC = rangeA.getExtension(rangeB);
        assertEquals(rangeC,new KVRange<>(6,8,false,true));
    }

    @Test
    public void testKVRangeTest_Inclusive_Inclusive(){
        KVRange<Integer> rangeA = new KVRange<>(0,5,false,true);
        KVRange<Integer> rangeB = new KVRange<>(2,3, true,true);
        assertEquals(true,rangeA.isInclusive(rangeB));
    }

    @Test
    public void testKVRangeTest_Inclusive_Exclusive(){
        KVRange<Integer> rangeA = new KVRange<>(0,5,false,true);
        KVRange<Integer> rangeB = new KVRange<>(0,1, true,true);
        assertEquals(false,rangeA.isInclusive(rangeB));
    }

    @Test
    public void testKVRangeTest_Inclusive_Wrap_sub(){
        KVRange<Integer> rangeA = new KVRange<>(5,0,true,true);
        KVRange<Integer> rangeB = new KVRange<>(1,4,true,true);
        assertEquals(false,rangeA.isInclusive(rangeB));
    }

    @Test
    public void testKVRangeTest_Inclusive_Wrap_intersect(){
        KVRange<Integer> rangeA = new KVRange<>(5,0,true,true);
        KVRange<Integer> rangeB = new KVRange<>(6,9,true,true);
        assertEquals(true,rangeA.isInclusive(rangeB));
    }

    @Test
    public void testKVRangeTest_Intersect(){
        KVRange<Integer> rangeA = new KVRange<>(0,5,false,true);
        KVRange<Integer> rangeB = new KVRange<>(1,6, true,true);
        assertEquals(true,rangeA.isIntersect(rangeB));
    }
}
