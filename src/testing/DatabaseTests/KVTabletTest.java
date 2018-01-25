package testing.DatabaseTests;

import database.cache.KVLFUCache;
import database.storage.KVTablet;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class KVTabletTest extends TestCase{
    KVTablet tablet = null;
    private static final String tablets_path = "tmp/";
    @Override
    public void setUp() throws Exception{
        tablet = KVTablet.createNewTablet(tablets_path);
    }

    @Override
    public void tearDown() throws  Exception{
        if((tablet!=null)){
            tablet.remove();
        }
    }

    @Test
    public void testInsertAndGet(){
        tablet.putToStorage("a","b");
        assertEquals("b",tablet.getFromStorage("a"));
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        tablet.putToStorage("a","b");
        tablet.store();
        UUID id = tablet.getID();
        tablet = null;
        tablet = KVTablet.load(tablets_path,id);
        assertEquals("b",tablet.getFromStorage("a"));
    }

    public static class KVLFUCacheTest extends TestCase {

        @Test
        public void testInsert() throws Exception {
            KVLFUCache cache = new KVLFUCache(5);
            cache.putToCache("1","2");
            assertEquals(cache.getFromCache("1"),"2");
        }
        @Test
        public void testUpdate() throws Exception {
            KVLFUCache cache = new KVLFUCache(5);
            cache.putToCache("1","2");
            cache.putToCache("1","3");
            assertEquals(cache.getFromCache("1"),"3");
        }
        @Test
        public void testDelete() throws Exception {
            KVLFUCache cache = new KVLFUCache(5);
            cache.putToCache("1","2");
            cache.putToCache("1","");
            assertEquals(cache.getFromCache("1"),"");
        }
        @Test
        public void testReplacement_one() throws Exception {
            KVLFUCache cache = new KVLFUCache(3);
            cache.putToCache("1","a");
            cache.putToCache("2","b");
            cache.putToCache("3","c");
            cache.getFromCache("1");
            cache.getFromCache("1");
            cache.getFromCache("3");
            cache.getFromCache("3");
            cache.getFromCache("2");
            cache.putToCache("4","d");
            assertEquals(cache.inCache("2"),false);
        }
        @Test
        public void testReplacement_two() throws Exception {
            KVLFUCache cache = new KVLFUCache(3);
            cache.putToCache("1","a");
            cache.putToCache("2","b");
            cache.putToCache("3","c");
            cache.getFromCache("2");
            cache.getFromCache("3");
            cache.getFromCache("1");
            cache.putToCache("4","d");
            assertEquals(cache.inCache("2"),false);
        }
    }
}
