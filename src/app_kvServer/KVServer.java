package app_kvServer;

import java.io.IOException;

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
	private  KVServerHandler serverHandler;
	private int port;
    private int cacheSize;
	private CacheStrategy cacheStrategy;
    public KVServer(int port, int cacheSize, String strategy) {
        this.port = port;
        serverHandler = createServerHandler();
        handlerThread = new Thread(serverHandler);
        handlerThread.start();
        this.cacheSize = cacheSize;
        cacheStrategy = CacheStrategy.fromString(strategy);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public boolean inCache(String key){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public String getKV(String key) throws Exception{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
    public void putKV(String key, String value) throws Exception{
		// TODO Auto-generated method stub
	}

	@Override
    public void clearCache(){
		// TODO Auto-generated method stub
	}

	@Override
    public void clearStorage(){
		// TODO Auto-generated method stub
	}

	@Override
    public void kill() throws InterruptedException, IOException {
		flushCache();
		serverHandler.stop();
		handlerThread.join();
	}

	@Override
    public void close() throws InterruptedException, IOException {
		kill();
	}

	@Override
    public void flushCache(){

    }

	/**
	 * Create a server handler (listener)
	 * @return a server handler instances
	 */
    public KVServerHandler createServerHandler(){
    	return new KVServerHandler(this.port, this,0);
	}

	public boolean isHandlerRunning(){
    	return this.serverHandler.isRunning();
	}

	public static void main(String[] args) {
	    int port = Integer.parseInt(args[0]);
	    int cachesize = Integer.parseInt(args[1]);
	    KVServer new_server = new KVServer(port,cachesize,args[2]);
	    while(true){

        }
	}
}
