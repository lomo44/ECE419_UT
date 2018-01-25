package app_kvServer;

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
	private CacheStrategy cacheStrategy;
	private KVDatabase database;
    public KVServer(int port, int cacheSize, String strategy) throws IOException, ClassNotFoundException {
        this.port = port;
        serverHandler = createServerHandler();
        handlerThread = new Thread(serverHandler);
        handlerThread.start();
        this.cacheSize = cacheSize;
        cacheStrategy = CacheStrategy.fromString(strategy);
        database = new KVDatabase(cacheSize,5000,strategy);
        // Pull the handler and check if the handler is running
        while(!serverHandler.isRunning()){
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
	    return this.cacheStrategy;
	}

	@Override
    public int getCacheSize(){
		// TODO Auto-generated method stub
		return -1;
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
		database.flushCache();
	}

	@Override
    public void clearStorage() throws IOException {
		database.flushStorage();
	}

	@Override
    public void kill() throws InterruptedException, IOException {
        serverHandler.stop();
        handlerThread.join();
        database.close();
    }

	@Override
    public void close() throws InterruptedException, IOException {
		kill();
	}

	@Override
    public void flushCache(){
		database.flushCache();
    }

	/**
	 * Create a server handler (listener)
	 * @return a server handler instances
	 */
    public KVServerHandler createServerHandler(){
    	return new KVServerHandler(this.port, this,1000);
	}

	public boolean isHandlerRunning(){
    	return this.serverHandler.isRunning();
	}

    public static void main(String[] args) {
        kv_out.enableLog("logs/server.log", Level.ALL);
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
}
