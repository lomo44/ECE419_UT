package database;

import java.util.concurrent.ConcurrentHashMap;

import database.cache.FIFOCache;
import database.cache.KVCache;
import database.cache.LRUCache;
import database.storage.KVStorage;
import database.storage.MMStorage;

import java.io.IOException;
import java.util.NoSuchElementException;
 

public class KVDatabase implements IKVDatabase {

	private int cacheSize;
	private KV_CacheStrategy cacheStrategy;
	private long storageSize;
	private KVCache cache;
	private KVStorage storage;
	
	public KVDatabase (int sizeofCache,long sizeofStorage,String cacheStrat) throws ClassNotFoundException, IOException {
		storageSize =sizeofStorage;
		cacheSize = sizeofCache;
		cacheStrategy = KV_CacheStrategy.fromString(cacheStrat);
		storage = new MMStorage(storageSize);
		switch(cacheStrategy) {
			default:LRU:
				cache = new LRUCache(cacheSize,"LRU") ;
			FIFO:
				cache = new FIFOCache(cacheSize,"FIFO");
			LFU:
				//not yet finished
				cache = null;
		}
	}
	


	@Override
	public synchronized String KV_getKV(String key) throws Exception {
			String value=cache.getFromCache(key);
			if (value==null) {
				value=storage.getFromStorage(key);
				cache.putToCache(key, value);
			}
			return value;
	}

	@Override
	public synchronized void KV_putKV(String key, String value) throws Exception {
			cache.putToCache(key, value);
			storage.putToStorage(key, value);
	}


	@Override
	public void KV_kill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void KV_close() {
		// TODO Auto-generated method stub
		
	}

}