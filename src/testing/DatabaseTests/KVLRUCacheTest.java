package testing.DatabaseTests;

import junit.framework.TestCase;
import org.junit.Test;

import database.cache.KVLRUCache;

public class KVLRUCacheTest extends TestCase {

	@Test
	public void testInsert() throws Exception {
		KVLRUCache cache = new KVLRUCache(5);
		cache.putToCache("1","2");
		assertEquals(cache.getFromCache("1"),"2");
	}
	@Test
	public void testUpdate() throws Exception {
		KVLRUCache cache = new KVLRUCache(5);
		cache.putToCache("1","2");
		cache.putToCache("1","3");
		assertEquals(cache.getFromCache("1"),"3");
	}
	@Test
	public void testDelete() throws Exception {
		KVLRUCache cache = new KVLRUCache(5);
		cache.putToCache("1","2");
		cache.putToCache("1","");
		assertEquals(cache.getFromCache("1"),"");
	}
	@Test
	public void testReplacement() throws Exception {
		KVLRUCache cache = new KVLRUCache(3);
		cache.putToCache("1","a");
		cache.putToCache("2","b");
		cache.putToCache("3","c");
		cache.getFromCache("1");
		cache.getFromCache("1");
		cache.getFromCache("1");
		cache.getFromCache("2");
		cache.putToCache("4","d");
		assertEquals(cache.inCache("3"),false);
	}

}
