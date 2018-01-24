package testing;

import database.cache.KVCache;
import junit.framework.TestCase;
import org.junit.Test;

import database.cache.KVFIFOCache;

public class KVFIFOCacheTest extends TestCase {

	@Test
	public void testInsert() throws Exception {
		KVCache cache = new KVFIFOCache(5);
		cache.putToCache("1","2");
		assertEquals(cache.getFromCache("1"),"2");
	}
	@Test
	public void testUpdate() throws Exception {
        KVCache cache = new KVFIFOCache(5);
		cache.putToCache("1","2");
		cache.putToCache("1","3");
		assertEquals(cache.getFromCache("1"),"3");
	}
	@Test
	public void testDelete() throws Exception {
        KVCache cache = new KVFIFOCache(5);
		cache.putToCache("1","2");
		cache.putToCache("1","");
		assertEquals(cache.getFromCache("1"),"");
	}
	@Test
	public void testReplacement() throws Exception {
        KVCache cache = new KVFIFOCache(3);
		cache.putToCache("1","a");
		cache.putToCache("2","b");
		cache.putToCache("3","c");
		cache.putToCache("4","d");
		assertEquals(cache.inCache("1"),false);
	}

}
