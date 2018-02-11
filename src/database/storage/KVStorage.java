package database.storage;

import java.io.IOException;

public interface KVStorage {
	/**
	 * Check if key is in storage. NOTE: does not modify any other properties
	 * 
	 * @return true if key in storage, false otherwise
	 */
	public boolean inStorage(String key);



	/**
	 * Get the value associated with the key
	 * 
	 * @return value associated with key
	 * @throws Exception
	 *             when key not in the key range of the server
	 */
	public String getFromStorage(String key) throws Exception;

	/**
	 * Put the key-value pair into storage
	 * 
	 * @throws Exception
	 *             when key not in the key range of the server
	 */
	public void putToStorage(String key, String value) throws Exception;



	/**
	 * Clear the storage of the server
	 */
	public void clearStorage() throws IOException;

	
	/**
	 * Abruptly stop the server without any additional actions NOTE: this includes
	 * performing saving to storage
	 */
}
