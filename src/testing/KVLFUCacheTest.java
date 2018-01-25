package testing;

import junit.framework.TestCase;
import org.junit.Test;

import database.cache.KVLFUCache;

public class KVLFUCacheTest extends TestCase {

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
