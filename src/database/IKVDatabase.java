package database;


import java.io.IOException;

public interface IKVDatabase {
	/**
	 * Get the value associated with the key
	 * 
	 * @return value associated with key
	 * @throws Exception
	 *             when key not in the key range of the server
	 */
	public String getKV(String key) throws Exception;

	/**
	 * Put the key-value pair into storage
	 * 
	 * @throws Exception
	 *             when key not in the key range of the server
	 */
	public void putKV(String key, String value) throws Exception;


	
	/**
	 * Abruptly stop the server without any additional actions NOTE: this includes
	 * performing saving to storage
	 */
	public void kill() throws IOException;

	/**
	 * Gracefully stop the server, can perform any additional actions
	 */
	public void close() throws IOException;

	public boolean inStorage(String key);

	public boolean inCache(String key);

	public void flushCache();
	public void flushStorage() throws IOException;
}
