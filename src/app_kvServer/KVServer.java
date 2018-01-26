package app_kvServer;

import common.enums.eKVExtendCacheType;
import common.enums.eKVLogLevel;
import database.KVDatabase;
import org.apache.log4j.Level;
import logger.KVOut;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class KVServer implements IKVServer {

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
    private Thread handlerThread;
    private KVServerHandler serverHandler;
    private static KVOut kv_out = new KVOut();
    private int port;
    private int cacheSize;
	private eKVExtendCacheType cacheStrategy;
	private KVDatabase database;
    public KVServer(int port, int cacheSize, String strategy) throws IOException, ClassNotFoundException {
        this.port = port;
        serverHandler = createServerHandler();
        handlerThread = new Thread(serverHandler);
        setLogLevel(eKVLogLevel.ALL,eKVLogLevel.DEBUG);
        kv_out.println_debug(String.format("Starting server at port %d, cache size: %d, stratagy: %s",port,cacheSize,strategy));
        handlerThread.start();
        this.cacheSize = cacheSize;
        cacheStrategy = eKVExtendCacheType.fromString(strategy);
        database = new KVDatabase(cacheSize,5000,strategy);
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

	@Override
	public int getPort(){
	    return port;
	}

	@Override
    public String getHostname(){
		return "localhost";
	}

	@Override
    public CacheStrategy getCacheStrategy(){
	    return this.cacheStrategy.toCacheStrategy();
	}

	@Override
    public int getCacheSize(){
        return cacheSize;
	}

	@Override
    public boolean inStorage(String key){
		return database.inStorage(key);
	}

	@Override
    public boolean inCache(String key){
		return database.inCache(key);
	}

	@Override
    public String getKV(String key) throws Exception{
		return database.getKV(key);
	}

	@Override
    public void putKV(String key, String value) throws Exception{
		database.putKV(key,value);
	}

	@Override
    public void clearCache(){
		kv_out.println_debug("Cache cleared");
        database.flushCache();
	}

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

    @Override
    public void run() {
	    kv_out.println_debug("Server running");
    }

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

	@Override
    public void close() {
        kill();
    }

    public void flushCache(){
		kv_out.println_debug("Cache flushed");
        database.flushCache();
    }

	/**
	 * Create a server handler (listener)
	 * @return a server handler instances
	 */
    public KVServerHandler createServerHandler(){
    	return new KVServerHandler(this.port, this,2000);
	}

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
}
