package app_kvServer;

import common.enums.eKVExtendCacheType;
import common.enums.eKVLogLevel;
import database.KVDatabase;
import org.apache.log4j.Level;
import logger.KVOut;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class KVServer implements IKVServer {


    private Thread handlerThread;
    private KVServerHandler serverHandler;
    private static KVOut kv_out = new KVOut("server");
    private int port;
    private int cacheSize;
	private eKVExtendCacheType cacheStrategy;
	private KVDatabase database;
    /**
     * Start KV Server at given port
     * @param port given port for storage server to operate
     * @param cacheSize specifies how many key-value pairs the server is allowed
     *           to keep in-memory
     * @param strategy specifies the cache replacement strategy in case the cache
     *           is full and there is a GET- or PUT-request on a key that is
     *           currently not contained in the cache. Options are "FIFO", "LRU",
     *           and "LFU".
     */
     
    public KVServer(int port, int cacheSize, String strategy) throws IOException, ClassNotFoundException {
        this.port = port;
        serverHandler = createServerHandler();
        handlerThread = new Thread(serverHandler);
        setLogLevel(eKVLogLevel.ALL,eKVLogLevel.DEBUG);
        kv_out.println_debug(String.format("Starting server at port %d, cache size: %d, stratagy: %s",port,cacheSize,strategy));
        handlerThread.start();
        this.cacheSize = cacheSize;
        cacheStrategy = eKVExtendCacheType.fromString(strategy);
        database = new KVDatabase(cacheSize,50000000,strategy);
        // Pull the handler and check if the handler is running
        while(!serverHandler.isRunning()){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.port = serverHandler.getPort();
    }
    /**
	 * Start KV Server with selected name
	 * @param name			unique name of server
	 * @param zkHostname	hostname where zookeeper is running
	 * @param zkPort		port where zookeeper is running
	 */
	public KVServer(String name, String zkHostname, int zkPort) {
		// TODO Auto-generated method stub
	}


    /**
     * Return the port that the server is open on
     * @return port number
     */
	@Override
	public int getPort(){
	    return port;
	}

    /**
     * Return the host name
     * @return string, hostname
     */
	@Override
    public String getHostname(){
		return "localhost";
	}

    /**
     * Return the cache strategy that server is using
     * @return
     */
	@Override
    public CacheStrategy getCacheStrategy(){
	    return this.cacheStrategy.toCacheStrategy();
	}

    /**
     * Get the cache size of the server
     * @return
     */
	@Override
    public int getCacheSize(){
        return cacheSize;
	}

    /**
     * Check if given key is in storage
     * @param key
     * @return
     */
	@Override
    public boolean inStorage(String key){
		return database.inStorage(key);
	}

    /**
     * Check if the key is in cache
     * @param key
     * @return
     */
	@Override
    public boolean inCache(String key){
		return database.inCache(key);
	}

    /**
     * Handle a get command
     * @param key key of the get command
     * @return value corresponds to the key
     * @throws Exception
     */
	@Override
    public String getKV(String key) throws Exception{
		return database.getKV(key);
	}

    /**
     * Handle a put command
     * @param key key of the put command
     * @param value value of the put command
     * @throws Exception
     */
	@Override
    public void putKV(String key, String value) throws Exception{
		database.putKV(key,value);
	}

    /**
     * Clear the cache
     */
	@Override
    public void clearCache(){
		kv_out.println_debug("Cache cleared");
        database.flushCache();
	}

    /**
     * Clear the storage
     */
	@Override
    public void clearStorage(){
        kv_out.println_debug("try to clear storage");
        try {
            database.flushStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        kv_out.println_debug("Storage cleared");
    }

    /**
     * Run the server
     */
    @Override
    public void run() {
	    kv_out.println_debug("Server running");
    }

    /**
     * Kill the server
     */
    @Override
    public void kill(){
        kv_out.println_debug("Try to kill server.");
        try {
            serverHandler.stop();
            handlerThread.join();
            database.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        kv_out.println_debug("Server Killed.");

    }

    /**
     * Gracefully close a server
     */
	@Override
    public void close() {
        kill();
    }

    /**
     * Flush the cache to storage
     */
    public void flushCache(){
		kv_out.println_debug("Cache flushed");
        database.flushCache();
    }

	/**
	 * Create a server handler (listener)
	 * @return a server handler instances
	 */
    public KVServerHandler createServerHandler(){
    	return new KVServerHandler(this.port, this);
	}

    /**
     * Check if the server handler is running
     * @return
     */
	public boolean isHandlerRunning(){
    	return this.serverHandler.isRunning();
	}

    public static void main(String[] args) {
        kv_out.enableLog("logs/server.log", Level.ALL);
        System.out.println("Run server from main");
        int port = Integer.parseInt(args[0]);
        int cachesize = Integer.parseInt(args[1]);
        try {
            KVServer new_server = new KVServer(port,cachesize,args[2]);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        while(true){

        }
	}

    /**
     * Change the log level of the server
     * @param outputlevel output level
     * @param logLevel log level
     */
	public void setLogLevel(eKVLogLevel outputlevel, eKVLogLevel logLevel){
	    kv_out.changeOutputLevel(outputlevel);
	    kv_out.changeLogLevel(outputlevel);
        serverHandler.setLogLevel(outputlevel,logLevel);
    }

    @Override
	public void start() {
		// TODO
	}

    @Override
    public void stop() {
		// TODO
	}

    @Override
    public void lockWrite() {
		// TODO
	}

    @Override
    public void unlockWrite() {
		// TODO
	}

    @Override
    public boolean moveData(String[] hashRange, String targetName) throws Exception {
		// TODO
		return false;
	}
}
