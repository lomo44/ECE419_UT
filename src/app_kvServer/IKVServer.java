package app_kvServer;

import common.communication.KVCommunicationModule;

import java.io.IOException;
import java.net.Socket;

public interface IKVServer {
    public enum CacheStrategy {
        None("None"),
        LRU("LRU"),
        LFU("LFU"),
        FIFO("FIFO");
        private String str;
        private CacheStrategy(String str){
            this.str = str;
        }
        public String toString(){
            return str;
        }
        public static CacheStrategy fromString(String str){
            switch (str){
                case "FIFO": return FIFO;
                case "LFU": return LFU;
                case "LRU": return LRU;
                default: return None;
            }
        }
    };

    /**
     * Get the port number of the server
     * @return  port number
     */
    public int getPort();

    /**
     * Get the hostname of the server
     * @return  hostname of server
     */
    public String getHostname();

    /**
     * Get the cache strategy of the server
     * @return  cache strategy
     */
    public CacheStrategy getCacheStrategy();

    /**
     * Get the cache size
     * @return  cache size
     */
    public int getCacheSize();

    /**
     * Check if key is in storage.
     * NOTE: does not modify any other properties
     * @return  true if key in storage, false otherwise
     */
    public boolean inStorage(String key);

    /**
     * Check if key is in storage.
     * NOTE: does not modify any other properties
     * @return  true if key in storage, false otherwise
     */
    public boolean inCache(String key);

    /**
     * Get the value associated with the key
     * @return  value associated with key
     * @throws Exception
     *      when key not in the key range of the server
     */
    public String getKV(String key) throws Exception;

    /**
     * Put the key-value pair into storage
     * @throws Exception
     *      when key not in the key range of the server
     */
    public void putKV(String key, String value) throws Exception;

    /**
     * Clear the local cache of the server
     */
    public void clearCache();

    /**
     * Clear the storage of the server
     */
    public void clearStorage();

//    /**
//     * Flush the local cache
//     */
//    public void flushCache();

    /**
     * Abruptly stop the server without any additional actions
     * NOTE: this includes performing saving to storage
     */
    public void kill() ;

    /**
     * Gracefully stop the server, can perform any additional actions
     */
    public void close();
}
