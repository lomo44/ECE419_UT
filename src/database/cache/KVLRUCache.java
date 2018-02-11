package database.cache;

import common.enums.eKVExtendCacheType;

import java.util.NoSuchElementException;
import java.util.Map;
import java.util.LinkedHashMap;


public class KVLRUCache extends KVCache {
	private Map<String, String> cache;
    private float loadfactor = 0.75f;

	
	public KVLRUCache(int size) {
		this.cacheSize = size;
		this.cacheStrategy = eKVExtendCacheType.LRU;
		cache = new LinkedHashMap<String,String>(cacheSize,loadfactor,true) {
            private static final long serialVersionUID = -1L;
			@Override
			 protected boolean removeEldestEntry(Map.Entry<String,String> eldest) {
                return size() > cacheSize;
			}
		};
	}
	
	public void printCache() {
		System.out.println(cache.toString());
	}
	
	@Override
	public boolean inCache(String key) {
		return this.cache.containsKey(key);
	}

	@Override
	public synchronized void clearCache() {
		cache.clear();
	}


	@Override
	public synchronized String getFromCache(String key) throws Exception {
        String value=cache.get(key);
        if (value==null) throw new NoSuchElementException("Key not found in cache.");
        return value;
	}


	@Override
	public synchronized void putToCache(String key, String value) throws Exception {
		cache.put(key, value);
	}

	@Override
	public void flushCache() {

	}

}