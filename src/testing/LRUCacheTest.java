package testing;

import static org.junit.Assert.*;

import org.junit.Test;

import database.cache.LRUCache;

public class LRUCacheTest {

	@Test
	public void test() {
		LRUCache cache= new LRUCache(5,"LRU") ;
		try {
		cache.putToCache("1", "1");
		cache.putToCache("2", "1");
		cache.putToCache("3", "1");
		cache.putToCache("4", "1");
		cache.putToCache("5", "1");
		cache.printCache();
		cache.putToCache("6", "6");
		System.out.print(cache.getFromCache("2"));
		cache.putToCache("7","7");
		System.out.print(cache.getFromCache("4"));
		cache.printCache();

		}
		catch (Exception e){
			System.out.println("problem occured when LRU put.");
		}
	}

}
