package database.cache;

import common.enums.eKVExtendCacheType;

public abstract class KVCache {
	protected eKVExtendCacheType cacheStrategy;
	protected int			  cacheSize;
	/**
	 * Get the cache strategy of the server
	 * 
	 * @return cache strategy
	 */
	public eKVExtendCacheType getCacheStrategy(){return cacheStrategy;};

	/**
	 * Get the cache size
	 * 
	 * @return cache size
	 */
	public int getCacheSize(){return cacheSize;}
	
	/**
	 * Check if key is in storage. NOTE: does not modify any other properties
	 * 
	 * @return true if key in storage, false otherwise
	 */
	public abstract boolean inCache(String key);
	
	/**
	 * Clear the local cache of the server
	 */
	public abstract void clearCache();
	
	/**
	 * Get the value associated with the key
	 * 
	 * @return value associated with key
	 * @throws Exception
	 *             when key not in the key range of the server
	 */
	public abstract String getFromCache(String key) throws Exception;

	/**
	 * Put the key-value pair into storage
	 * 
	 * @throws Exception
	 *             when key not in the key range of the server
	 */
	public abstract void putToCache(String key, String value) throws Exception;
	
	/**
	 * Flush the local cache
	 */
	public abstract void flushCache();

}
