package database.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;


public class FIFOCache implements KVCache{

	private int cacheSize;
	private KV_CacheStrategy cacheStrategy;
	private Map<String, String> cache;
    private float loadfactor = 0.75f;

	
	public FIFOCache(int size, String strategy) {
		this.cacheSize = size;
		this.cacheStrategy = KV_CacheStrategy.fromString("FIFO");
		cache = new LinkedHashMap<String,String>(cacheSize,loadfactor,false) {
            private static final long serialVersionUID = -2L;
			@Override
			 protected boolean removeEldestEntry(Map.Entry<String,String> eldest) {
                return size() > cacheSize;
			}
		};
	}
	
	
	@Override
	public KV_CacheStrategy KV_getCacheStrategy() {
	    return cacheStrategy;
	}
	
	@Override
	public int KV_getCacheSize() {
		return cacheSize;
	}
	
	@Override
	public boolean KV_inCache(String key) {
		return cache.containsKey(key);
	}
	
	public void printCache() {
		System.out.println(cache.toString());
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
	public synchronized String getFromCache(String key) throws Exception {
        String value=cache.get(key);
        if (value==null) throw new NoSuchElementException("Key not found in cache.");
        return value;
	}


	@Override
	public synchronized void putToCache(String key, String value) throws Exception {
		cache.put(key, value);
	}
	
	
}