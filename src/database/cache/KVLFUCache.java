package database.cache;

import java.util.NoSuchElementException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;


public class KVLFUCache extends KVCache {

	private class Node{
		String value;
		int hitcount;
		public Node(String value) {
			this.value = value;
			hitcount=0;
		}
	}
	
	private int cacheSize;
	private KVCacheStrategy cacheStrategy;
	private Map<String, Node> cache;
	private Map<Integer,LinkedHashMap<String,String>> HitCounttoKey;
	private int lowestHitCount;

	
	public KVLFUCache(int size) {
		cacheSize = size;
		cacheStrategy = KVCacheStrategy.fromString("LFU");
		cache = new ConcurrentHashMap<>();
		HitCounttoKey = new ConcurrentHashMap<>();
		lowestHitCount = 0;
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
		HitCounttoKey.clear();
	}
	
	@Override
	public synchronized void flushCache() {
		cache.clear();
		HitCounttoKey.clear();
	}
	
	@Override
	public synchronized String getFromCache(String key) throws Exception {
        Node node=cache.get(key);
        if (node == null) 
    			throw new NoSuchElementException("Key not found in cache.");
		removeFromHitMap(key,node.hitcount);
		putToHitMap(key,++node.hitcount);
		cache.put(key, node);
        return node.value;
	}

	@Override
	public synchronized void putToCache(String key, String value) throws Exception {
		if (cache.size()==cacheSize) //evict if cache is full
			cache.remove(removeFromHitMap(null,lowestHitCount));
		Node node = new Node(value);
		putToHitMap(key,node.hitcount);
		cache.put(key, node);
	}
	
	private void putToHitMap(String key,int hitcount) throws NoSuchElementException{
		LinkedHashMap<String,String> keylist = HitCounttoKey.get(hitcount);
		if (keylist==null) {
			keylist = new LinkedHashMap<>(16,0.75f,true);
			if (hitcount<lowestHitCount)
				lowestHitCount=hitcount;
		}
		keylist.put(key,key);
		HitCounttoKey.put(hitcount, keylist);
	}
	
	private String removeFromHitMap(String key,int hitcount) throws NoSuchElementException{
		LinkedHashMap<String,String> keylist = HitCounttoKey.get(hitcount);
		if (key==null) //means cache is evicting due to cache full
			key=keylist.keySet().iterator().next();
		if (keylist==null) 
			throw new NoSuchElementException("Key not found in HitcounttoKey");
		if (keylist.remove(key)==null)
			throw new NoSuchElementException("key not found in keylist.");
		if(keylist.isEmpty()) {
			if (hitcount == lowestHitCount)
				lowestHitCount++;
			HitCounttoKey.remove(hitcount);
		}
		return key;
	}
}