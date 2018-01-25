package database;

import database.cache.KVFIFOCache;
import database.cache.KVLFUCache;
import database.cache.KVCache;
import database.cache.KVLRUCache;
import database.storage.KVStorage;
import database.storage.MMStorage;

import java.io.IOException;


public class KVDatabase implements IKVDatabase {

	private int cacheSize;
	private KVCache.KVCacheStrategy cacheStrategy;
	private long storageSize;
	private KVCache cache;
	private KVStorage storage;
	
	public KVDatabase (int sizeofCache,long sizeofStorage,String cacheStrat) throws ClassNotFoundException, IOException {
		storageSize =sizeofStorage;
		cacheSize = sizeofCache;
		cacheStrategy = KVCache.KVCacheStrategy.fromString(cacheStrat);
		storage = new MMStorage(storageSize);
		switch(cacheStrategy) {
			case FIFO:
				cache = new KVFIFOCache(cacheSize);
				break;
			case LRU:
				cache = new KVLRUCache(cacheSize) ;
				break;
			default:case LFU:
				cache = new KVLFUCache(cacheSize);
		}
	}
	


	@Override
	public synchronized String getKV(String key) throws Exception {
		String value= null;
		try {
			value = cache.getFromCache(key);
		} catch (Exception e) {
			try {
				value=storage.getFromStorage(key);
				cache.putToCache(key, value);
				return value;
			} catch (Exception e1) {
				throw e1;
			}
		}
		return value;
	}

	@Override
	public synchronized void putKV(String key, String value) throws Exception {
			cache.putToCache(key, value);
			storage.putToStorage(key, value);
	}


	@Override
	public void kill() throws IOException {
		cache.clearCache();
	}

	@Override
	public void close() throws IOException {
		cache.clearCache();
	}

	@Override
	public boolean inStorage(String key) {
		return storage.inStorage(key);
	}

	@Override
	public boolean inCache(String key) {
		return cache.inCache(key);
	}

	@Override
	public void flushCache() {
		cache.flushCache();
	}

	@Override
	public void flushStorage() throws IOException {
		storage.clearStorage();
	}

}