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
}
