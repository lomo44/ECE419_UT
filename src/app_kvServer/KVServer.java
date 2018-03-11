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

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class KVServer implements IKVServer {

    private static KVOut kv_out = new KVOut("server");
    private Thread handlerThread;
    private KVServerHandler serverHandler;
    private KVServerDaemon serverDaemon;
    private Thread serverDaemonThread;
	private KVDatabase database;
	private KVServerConfig config;
    private eKVServerStatus serverStatus = eKVServerStatus.STOPPED;
	private KVMetadataController metadataController = new KVMetadataController();
	private KVMigrationModule migrationModule = new KVMigrationModule();
	private ZKClient zkClient;
	private String UID = "tmp";
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
        UID = serverName;
        KVServerConfig config = new KVServerConfig();
        config.setCacheSize(cacheSize);
        config.setServerPort(port);
        config.setCacheStratagy(strategy);
        try {
            initializeServer(config,null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setLogLevel(eKVLogLevel.OFF,eKVLogLevel.OFF);
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
        this.UID = name;
	    //kv_out.println_debug(String.format("Starting server at port %d, cache size: %d, stratagy: %s",port,cacheSize,strategy));
        try {
            zkClient = new ZKClient(zkHostname+":"+Integer.toString(zkPort),name,this);
        } catch (Exception e){
            e.printStackTrace();
        }
        //zkClient.connect();
        //KVServerConfig config = zkClient.getCurrentServerConfig();
	}

	public void initializeServer(KVServerConfig config, KVMetadata metadata) throws InterruptedException {
	    this.config = config;
		database = new KVDatabase(config.getCacheSize(),50000000,config.getCacheStratagy(),this.UID);
        this.serverDaemon = new KVServerDaemon(this);
        this.serverDaemonThread = new Thread(this.serverDaemon);
		serverHandler = createServerHandler();
        setLogLevel(eKVLogLevel.ALL,eKVLogLevel.DEBUG);
        handlerThread = new Thread(serverHandler);
        handlerThread.start();


        // Pull the handler and check if the handler is running
        while(!serverHandler.isRunning()){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.serverDaemonThread.start();
        this.config.setServerPort(serverHandler.getPort());
        metadataController.addStorageNode(new KVStorageNode(getHostAddress(),getPort(),getServername()));
        if(metadata!=null){
            metadataController.update(metadata);
        }


    }


	public String getServername() {
		return UID;
	}
	
    /**
     * Return the port that the server is open on
     * @return port number
     */
	@Override
	public int getPort(){
	    return this.config.getServerPort();
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
	    return eKVExtendCacheType.fromString(config.getCacheStratagy()).toCacheStrategy();
	}

    /**
     * Get the cache size of the server
     * @return
     */
	@Override
    public int getCacheSize(){
        return this.config.getCacheSize();
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
        close();
    }

    /**
     * Gracefully close a server
     */
	@Override
    public void close() {
        this.serverDaemonThread.interrupt();
        try {
            this.serverDaemonThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeASync() {
	    this.serverDaemonThread.interrupt();

    }

    public void daemonShutdownHandle(){
        kv_out.println_debug("Try to kill server.");
        lockWrite();
        stop();
        metadataController.removeStorageNode(this.getNetworkNode());
        migrateData();
        unlockWrite();
        try {

            serverHandler.stop();
            handlerThread.join();
            database.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        kv_out.println_debug("Server Killed.");
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
    	return new KVServerHandler(this.config.getServerPort(), this);
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
        for(String str :args){
            System.out.println(str);
        }
        KVServer new_server = new KVServer(args[0],args[1],Integer.parseInt(args[2]));
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
	    return new KVNetworkNode(serverHandler.getHostName(),serverHandler.getPort());
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

	private void migrateData(){
        // Filter out migrating data
        Set<String> keys = this.database.getKeys();
        if (keys.size() > 0) {
            // assort key to different node
            //System.out.println(String.format("Migration Sender starts %s",this.getNetworkNode().toString()));
            HashMap<KVStorageNode, Set<String>> msgtable = new HashMap<>();
            for (String key: keys
                    ) {
                KVStorageNode node = metadataController.getResponsibleStorageNode(metadataController.hash(key));
                String k =this.getNetworkNode().toString();
                if(node!=null && !node.toString().matches(k)){
                    if(!msgtable.containsKey(node))
                        msgtable.put(node,new HashSet<String>());
                    msgtable.get(node).add(key);
                }
            }
            if(msgtable.size()>0){
                // Formulate migration msg and start migration
                List<KVStorageNode> storageNodes = metadataController.getStorageNodes();
                for(KVStorageNode node: msgtable.keySet()){
                    KVMigrationMessage msg = new KVMigrationMessage();
                    for(String key: msgtable.get(node)){
                        try {
                            msg.add(key,database.getKV(key));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    KVJSONMessage ret = new KVJSONMessage();
                    ret.setExtendStatus(eKVExtendStatusType.MIGRATION_INCOMPLETE);
                    try {
                        ret = migrationModule.migrate(node,msg);
                    } catch (IOException e) {
                    }
                    if(ret.getExtendStatusType()==eKVExtendStatusType.MIGRATION_INCOMPLETE){
                        for (KVStorageNode possibleNode: storageNodes
                                ) {
                            try {
                                ret = migrationModule.migrate(possibleNode,msg);
                                if(ret.getExtendStatusType() == eKVExtendStatusType.MIGRATION_COMPLETE){
                                    return;
                                }
                            } catch (IOException e) {
                            }
                        }
                        System.out.println("Migration Incomplete, possible data lost");
                    }
                }
                //System.out.println("Migration Sender ends.");
            }
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
                        lockWrite();
                        migrateData();
                        unlockWrite();
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
            database = new KVDatabase(newConfig.getCacheSize(),5000000,newConfig.getCacheStratagy(), UID);
        }
    }

	public boolean isKeyResponsible(String key){
//	    System.out.println(String.format("Upper: %s",metadataController.getStorageNode(getNetworkNode()).getHashRangeString().getUpperBound().toString()));
//	    System.out.println(String.format("Key  : %s",metadataController.hash(key)));
//        System.out.println(String.format("Lower: %s",metadataController.getStorageNode(getNetworkNode()).getHashRangeString().getLowerBound().toString()));
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
