package app_kvServer;

import common.datastructure.KVRange;
import common.enums.eKVExtendCacheType;
import common.enums.eKVExtendStatusType;
import common.enums.eKVLogLevel;
import common.enums.eKVServerStatus;
import common.messages.KVJSONMessage;
import common.messages.KVMigrationMessage;
import common.metadata.KVMetadata;
import common.metadata.KVMetadataController;
import common.networknode.KVNetworkNode;
import common.networknode.KVStorageNode;
import common.zookeeper.ZKClient;
import database.KVDatabase;
import org.apache.log4j.Level;
import logger.KVOut;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class KVServer implements IKVServer {

    private static KVOut kv_out = new KVOut("server");
    private Thread handlerThread;
    private KVServerHandler serverHandler;

    private int port;
    private int cacheSize;
	private KVDatabase database;
    private eKVExtendCacheType cacheStrategy;
    private eKVServerStatus serverStatus = eKVServerStatus.STOPPED;
	private KVMetadataController metadataController = new KVMetadataController();
	private KVMigrationModule migrationModule = new KVMigrationModule();
	private ZKClient zkClient;
	private String uniqueName = "tmp";
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
     
    public KVServer(int port, int cacheSize, String strategy, String serverName) throws IOException, ClassNotFoundException {

        kv_out.println_debug(String.format("Starting server at port %d, cache size: %d, stratagy: %s",port,cacheSize,strategy));
        uniqueName = serverName;
        this.port = port;
        serverHandler = createServerHandler();

        setLogLevel(eKVLogLevel.OFF,eKVLogLevel.OFF);
        handlerThread = new Thread(serverHandler);
        handlerThread.start();

        this.cacheSize = cacheSize;
        cacheStrategy = eKVExtendCacheType.fromString(strategy);
        database = new KVDatabase(cacheSize,50000000,strategy,"./"+serverName);
        // Pull the handler and check if the handler is running
        while(!serverHandler.isRunning()){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.port = serverHandler.getPort();
        metadataController.addStorageNode(new KVStorageNode(getHostAddress(),getPort(),getServername()));
        serverStatus = eKVServerStatus.STARTED;
    }

    public KVServer(int port, int cacheSize, String strategy) throws IOException, ClassNotFoundException {
        this(port,cacheSize,strategy,"tmp");
    }
    /**
	 * Start KV Server with selected name
	 * @param name			unique name of server
	 * @param zkHostname	hostname where zookeeper is running
	 * @param zkPort		port where zookeeper is running
	 */
	public KVServer(String name, String zkHostname, int zkPort) {
        this.uniqueName = name;
        this.port = zkPort;
	    //kv_out.println_debug(String.format("Starting server at port %d, cache size: %d, stratagy: %s",port,cacheSize,strategy));
        try {
            zkClient = new ZKClient(zkHostname+":"+Integer.toString(zkPort),name,this);
        } catch (Exception e){
            e.printStackTrace();
        }
        //zkClient.connect();
        //KVServerConfig config = zkClient.getCurrentServerConfig();
	}

	public void initializeServer(KVServerConfig config, KVMetadata metadata){
        database = new KVDatabase(config.getCacheSize(),50000000,config.getCacheStratagy(),this.uniqueName);
        serverHandler = createServerHandler();
        setLogLevel(eKVLogLevel.ALL,eKVLogLevel.DEBUG);
        handlerThread = new Thread(serverHandler);
        handlerThread.start();
        // Pull the handler and check if the handler is running
        while(!serverHandler.isRunning()){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.port = serverHandler.getPort();
        metadataController.addStorageNode(new KVStorageNode(getHostAddress(),getPort(),getServername()));
        metadataController.update(metadata);
    }


	public String getServername() {
		return uniqueName;
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

	public String getHostAddress() {return serverHandler.getHostAddress();}

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
            KVServer new_server = new KVServer(port,cachesize,args[2],"local");
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

    public KVNetworkNode getNetworkNode(){
	    return serverHandler.getNetworkNode();
    }

    @Override
	public void start() {
		serverStatus = eKVServerStatus.STARTED;

	}

    @Override
    public void stop() {
		// TODO
        serverStatus = eKVServerStatus.STOPPED;
	}

    @Override
    public void lockWrite() {
        this.database.lockWrite();
	}

    @Override
    public void unlockWrite() {
        this.database.unlockWrite();
	}

	public void lockRead() { this.database.lockRead();}

	public void unlockRead(){this.database.unlockRead();}

    @Override
    public boolean moveData(String[] hashRange, String targetName) throws Exception {
		boolean ret = false;
	    lockWrite();
	    // Fetch the corresponding node from the meta data controller.
	    KVStorageNode node = metadataController.getStorageNode(targetName);
		if(node!=null){
            KVRange<BigInteger> range = KVRange.fromString(hashRange[0],hashRange[1],true,false);
            KVMigrationMessage migrationMessage = new KVMigrationMessage();
            Set<String> keys = database.getKeys();
            for (String key: keys
                    ) {
                if(range.inRange(metadataController.hash(key))){
                    migrationMessage.add(key,database.getKV(key));
                }
            }
            KVJSONMessage response = migrationModule.migrate(node,migrationMessage);
            if(response.getExtendStatusType()== eKVExtendStatusType.MIGRATION_COMPLETE){
                ret = true;
            }
            else{
                ret = false;
            }
        }
        unlockWrite();
		return ret;
	}

	private void migrateData() throws Exception {
        // Filter out migrating data
        Set<String> keys = this.database.getKeys();
        if (keys.size() > 0) {
            // assort key to different node
            lockWrite();
            //System.out.println(String.format("Migration Sender starts %s",this.getNetworkNode().toString()));
            HashMap<KVStorageNode, Set<String>> msgtable = new HashMap<>();
            for (String key: keys
                    ) {
                KVStorageNode node = metadataController.getResponsibleStorageNode(metadataController.hash(key));
                if(node!=null && !node.toString().matches(this.getNetworkNode().toString())){
                    if(!msgtable.containsKey(node))
                        msgtable.put(node,new HashSet<>());
                    msgtable.get(node).add(key);
                }
            }
            if(msgtable.size()>0){
                // Formulate migration msg and start migration
                for(KVStorageNode node: msgtable.keySet()){
                    KVMigrationMessage msg = new KVMigrationMessage();
                    for(String key: msgtable.get(node)){
                        msg.add(key,database.getKV(key));
                    }
                    KVJSONMessage ret = new KVJSONMessage();
                    ret.setExtendStatus(eKVExtendStatusType.MIGRATION_INCOMPLETE);
                    while(ret.getExtendStatusType()==eKVExtendStatusType.MIGRATION_INCOMPLETE){
                        ret = migrationModule.migrate(node,msg);
                    }
                }
                //System.out.println("Migration Sender ends.");
            }
            unlockWrite();
        }
    }

    public void handleChangeInMetadata(KVMetadata newMetadata) throws Exception {
	    if(!newMetadata.equals(metadataController.getMetaData())){
	        // meta data change, new node added
            KVRange<BigInteger> oldRange = metadataController.getStorageNode(getHostAddress(),getPort()).getHashRange();
            metadataController.update(newMetadata);
            KVRange<BigInteger> newRange = metadataController.getStorageNode(getHostAddress(),getPort()).getHashRange();
            if(!oldRange.equals(newRange)){
                // Hash range update, need to do migration
                if(!newRange.isInclusive(oldRange)){
                    // range reduce or moved. for now we only consider reduction
                    KVRange<BigInteger> reducedRange = oldRange.getExtension(newRange);
                    if(reducedRange!=null){
                        migrateData();
                    }
                    else{
                        kv_out.println_fatal("Hash Range Error. ");
                    }
                }
            }
        }
    }

    public void handleChangeInConfigData(KVServerConfig newConfig){
        if(database==null){
            database = new KVDatabase(newConfig.getCacheSize(),5000000,newConfig.getCacheStratagy(),uniqueName);
        }
    }

	public boolean isKeyResponsible(String key){
//	    System.out.println(String.format("Upper: %s",metadataController.getStorageNode(getNetworkNode()).getHashRange().getUpperBound().toString()));
//	    System.out.println(String.format("Key  : %s",metadataController.hash(key)));
//        System.out.println(String.format("Lower: %s",metadataController.getStorageNode(getNetworkNode()).getHashRange().getLowerBound().toString()));
        boolean ret = metadataController.getStorageNode(getHostAddress(),getPort()).isResponsible(metadataController.hash(key));
//        System.out.println(String.format("In range: %b",ret));
//        System.out.println("-------------------------------------------------");
        return ret;
    }

    public KVMetadata getCurrentMetadata(){
	    return metadataController.getMetaData();
    }

    public boolean isStopped(){
	    return serverStatus == eKVServerStatus.STOPPED;
    }

    public KVOut getLogger() {
        return kv_out;
    }
}
