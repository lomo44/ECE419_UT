package database.cache;

import java.util.NoSuchElementException;
import java.util.Map;
import java.util.LinkedHashMap;


public class LFUCache implements KVCache{

	private int cacheSize;
	private KV_CacheStrategy cacheStrategy;
	private Map<String, String> cache;
    private float loadfactor = 0.75f;

	
	public LFUCache(int size, String strategy) {
		this.cacheSize = size;
		this.cacheStrategy = KV_CacheStrategy.fromString("LRU");
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
	public KV_CacheStrategy KV_getCacheStrategy() {
	    return this.cacheStrategy;
	}
	
	@Override
	public int KV_getCacheSize() {
		return this.cacheSize;
	}
	
	@Override
	public boolean KV_inCache(String key) {
		return this.cache.containsKey(key);
	}
	
	
	
	@Override
	public synchronized void KV_clearCache() {
		cache.clear();
	}
	
	@Override
	public synchronized void KV_flushCache() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized String getFromCache(String key) throws NoSuchElementException {
        String value=cache.get(key);
        if (value==null) throw new NoSuchElementException("Key not found in cache.");
        return value;
	}


	@Override
	public synchronized void putToCache(String key, String value) throws Exception {
		cache.put(key, value);
	}
	
	
}