package database.cache;

public interface KVCache {
	
	public enum KV_CacheStrategy {
		None("None"), LRU("LRU"), LFU("LFU"), FIFO("FIFO");
		private String str;

		private KV_CacheStrategy(String str) {
			this.str = str;
		}

		public String toString() {
			return str;
		}

		public static KV_CacheStrategy fromString(String str) {
			switch (str) {
			case "FIFO":
				return FIFO;
			case "LFU":
				return LFU;
			case "LRU":
				return LRU;
			default:
				return None;
			}
		}
	};
	
	/**
	 * Get the cache strategy of the server
	 * 
	 * @return cache strategy
	 */
	public KV_CacheStrategy KV_getCacheStrategy();

	/**
	 * Get the cache size
	 * 
	 * @return cache size
	 */
	public int KV_getCacheSize();
	
	/**
	 * Check if key is in storage. NOTE: does not modify any other properties
	 * 
	 * @return true if key in storage, false otherwise
	 */
	public boolean KV_inCache(String key);
	
	/**
	 * Clear the local cache of the server
	 */
	public void KV_clearCache();
	
	/**
	 * Get the value associated with the key
	 * 
	 * @return value associated with key
	 * @throws Exception
	 *             when key not in the key range of the server
	 */
	public String getFromCache(String key) throws Exception;

	/**
	 * Put the key-value pair into storage
	 * 
	 * @throws Exception
	 *             when key not in the key range of the server
	 */
	public void putToCache(String key, String value) throws Exception;
	
	/**
	 * Flush the local cache
	 */
	public void KV_flushCache();

}
