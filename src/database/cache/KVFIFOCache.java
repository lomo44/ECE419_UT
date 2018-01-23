package database.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;


public class KVFIFOCache extends KVCache{

	private int cacheSize;
	private KVCacheStrategy cacheStrategy;
	private Map<String, String> cache;
    private float loadfactor = 0.75f;

	
	public KVFIFOCache(int size) {
		this.cacheSize = size;
		this.cacheStrategy = KVCacheStrategy.fromString("FIFO");
		cache = new LinkedHashMap<String,String>(cacheSize,loadfactor,false) {
            private static final long serialVersionUID = -2L;
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
		return cache.containsKey(key);
	}

	@Override
	public synchronized void clearCache() {
		cache.clear();
	}
	
	@Override
	public synchronized void flushCache() {
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