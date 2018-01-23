package database;


public interface IKVDatabase {

	
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
	 * Get the value associated with the key
	 * 
	 * @return value associated with key
	 * @throws Exception
	 *             when key not in the key range of the server
	 */
	public String KV_getKV(String key) throws Exception;

	/**
	 * Put the key-value pair into storage
	 * 
	 * @throws Exception
	 *             when key not in the key range of the server
	 */
	public void KV_putKV(String key, String value) throws Exception;


	
	/**
	 * Abruptly stop the server without any additional actions NOTE: this includes
	 * performing saving to storage
	 */
	public void KV_kill();

	/**
	 * Gracefully stop the server, can perform any additional actions
	 */
	public void KV_close();

}
