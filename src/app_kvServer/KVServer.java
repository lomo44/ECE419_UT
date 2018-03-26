package app_kvServer;

import app_kvServer.daemons.KVServerShutdownDaemon;
import common.datastructure.KVRange;
import common.enums.*;
import common.messages.KVJSONMessage;
import common.messages.KVMigrationMessage;
import common.metadata.KVMetadata;
import common.metadata.KVMetadataController;
import common.networknode.KVStorageCluster;
import common.networknode.KVStorageNode;
import common.zookeeper.ZKClient;
import database.KVDatabase;
import logger.KVOut;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class KVServer implements IKVServer {

    private KVOut kv_out;
    private KVServerHandler serverHandler;
    private KVServerShutdownDaemon serverExitDaemon;
    private KVDatabase database;
    private KVServerConfig config;
    private KVStorageNode node;
    private KVMetadataController metadataController = new KVMetadataController();
    private KVMigrationModule migrationModule = new KVMigrationModule();
    private KVClusterCommunicationModule clusterCommunicationModule;
    private Thread handlerThread;
    private Thread serverDaemonThread;
    private eKVServerStatus serverStatus = eKVServerStatus.STOPPED;
	private ZKClient zkClient;
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
     
    public KVServer(int port, int cacheSize, String strategy, String serverName){
        KVServerConfig config = new KVServerConfig();
        config.setCacheSize(cacheSize);
        config.setServerPort(port);
        config.setCacheStratagy(strategy);
        config.setServerHostAddress("localhost");
        config.setServerName(serverName);
        try {
            initializeServer(config,null,null);
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
	    //kv_out.println_debug(String.format("Starting server at port %d, cache size: %d, stratagy: %s",port,cacheSize,strategy));
        try {
            zkClient = new ZKClient(zkHostname+":"+Integer.toString(zkPort),name,this);
        } catch (Exception e){
            e.printStackTrace();
        }
        //zkClient.connect();
        //KVServerConfig config = zkClient.getCurrentServerConfig();
	}

	public void initializeServer(KVServerConfig config, KVMetadata metadata, ZKClient client) throws InterruptedException {
	    this.kv_out = new KVOut(config.getServerHostAddress());
	    this.config = config;
	    //System.out.println(new String(config.toKVJSONMessage().toBytes()));
		database = new KVDatabase(config.getCacheSize(),50000000,config.getCacheStratagy(),config.getServerName());
        this.serverExitDaemon = new KVServerShutdownDaemon(this);
        this.serverDaemonThread = new Thread(this.serverExitDaemon);
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
        this.clusterCommunicationModule = new KVClusterCommunicationModule(this);
        this.serverDaemonThread.start();
        this.config.setServerPort(serverHandler.getPort());
        //metadataController.addStorageNode(new KVStorageNode(getHostAddress(),getPort(),getServername()));
        if(metadata!=null){
            metadataController.update(metadata);
        }
        else{
            metadataController.update(new KVMetadata());
        }
        this.zkClient = client;
        if(zkClient!=null){
            for(String cluster : config.getBelongedCluster()){
                joinCluster(cluster);
            }
        }
    }

	public String getUID() {
		return config.getServerName();
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
     * Gracefully stop a server
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
        try {
            serverHandler.stop();
            handlerThread.join();
            Collection<KVStorageNode> nodes = metadataController.getReleventNodes(this.getUID());
            boolean needMigrateData = false;
            for(KVStorageNode node : nodes){
                switch (node.getNodeType()){
                    case STORAGE_NODE:{
                        metadataController.removeStorageNode(this.getUID());
                        needMigrateData = true;
                        break;
                    }
                    case STORAGE_CLUSTER:{
                        KVStorageCluster cluster = (KVStorageCluster) node;
                        cluster.removeNodeByUID(this.getUID());
                        if(cluster.isEmpty()){
                            metadataController.removeStorageNode(cluster.getUID());
                            needMigrateData = true;
                        }
                    }
                }
            }
            if(needMigrateData){
                migrateData();
            }
            unlockWrite();
            database.close();
            if(zkClient!=null){
                zkClient.close();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.clusterCommunicationModule.close();
        this.migrationModule.close();
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

	public void lockRead() {
	    this.database.lockRead();}

	public void unlockRead(){
	    this.database.unlockRead();
	}

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
            HashMap<String, String> migrationEntries = new HashMap<>();
            for (String key: keys
                    ) {
                if(range.inRange(metadataController.hash(key))){
                    migrationEntries.put(key,database.getKV(key));
                }
            }
            migrationMessage.setEntries(migrationEntries);
            migrationMessage.setTargetName(targetName);
            KVJSONMessage response = migrationModule.clusterExternalMigration(node,migrationMessage);
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
    
    public boolean moveData( KVRange<BigInteger> range, KVStorageNode node) throws Exception {
		boolean ret = false;
	    lockWrite();
	    // Fetch the corresponding node from the meta data controller.
		if(node!=null){
            KVMigrationMessage migrationMessage = new KVMigrationMessage();
            Set<String> keys = database.getKeys();
            HashMap<String, String> migrationEntries = new HashMap<>();
            for (String key: keys
                    ) {
                if(range.inRange(metadataController.hash(key))){
                    migrationEntries.put(key,database.getKV(key));
                }
            }
            migrationMessage.setEntries(migrationEntries);
            migrationMessage.setTargetName(node.getUID());
            KVJSONMessage response = migrationModule.syncPrimaryForwardMigration(node,migrationMessage);
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
            HashMap<KVStorageNode, HashMap<String,String>> msgtable = new HashMap<>();
            String k =this.getStorageNode().toString();
            for (String key: keys
                    ) {
                KVStorageNode node = metadataController.getResponsibleStorageNode(metadataController.hash(key));
                if(node!=null){
                    boolean shouldMigrate = true;
                    switch (node.getNodeType()){
                        case STORAGE_NODE:{
                            if(node.getUID().matches(this.getUID())){
                                shouldMigrate = false;
                            }
                            break;
                        }
                        case STORAGE_CLUSTER:{
                            KVStorageCluster cluster = (KVStorageCluster)node;
                            if(cluster.contain(this.getUID())){
                                shouldMigrate = false;
                            }
                        }
                    }
                    if(shouldMigrate) {
                        if (!msgtable.containsKey(node)) {
                            msgtable.put(node, new HashMap<String, String>());
                        }
                        try {
                            String value = database.getKV(key);
                            if (!value.matches("")) {
                                msgtable.get(node).put(key, value);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            if(msgtable.size()>0){
                // Formulate migration msg and start migration
                Collection<KVStorageNode> storageNodes = metadataController.getStorageNodes();
                for(KVStorageNode node: msgtable.keySet()){
                    KVMigrationMessage msg = new KVMigrationMessage();
                    msg.setEntries(msgtable.get(node));
                    msg.setTargetName(node.getUID());
                    msg.setIsRequiredAck(true);
                    KVJSONMessage ret = new KVJSONMessage();
                    ret.setExtendStatus(eKVExtendStatusType.MIGRATION_INCOMPLETE);
                    try {
                        ret = migrationModule.clusterExternalMigration(node,msg);
                        if(ret.getExtendStatusType()==eKVExtendStatusType.MIGRATION_INCOMPLETE){
                            for (KVStorageNode possibleNode: storageNodes
                                    ) {
                                ret = migrationModule.clusterExternalMigration(possibleNode,msg);
                                if(ret.getExtendStatusType() == eKVExtendStatusType.MIGRATION_COMPLETE){
                                    break;
                                }
                            }
                            System.out.println("Migration Incomplete, possible data lost");
                        }
                    } catch (IOException e) {
                    }
                }
                //System.out.println("Migration Sender ends.");
            }
        }
    }

    public void handleChangeInMetadata(KVMetadata newMetadata) throws Exception {
        if(metadataController.getStorageNode(this.getUID())==null && newMetadata.getStorageNodeFromHash(metadataController.hash(this.getUID()))!=null){
            // Metadata has more information than I do
            metadataController.addStorageNode(newMetadata.getStorageNodeFromHash(metadataController.hash(this.getUID())));
        }
//        Collection<KVStorageNode> irrelevantNodes = newMetadata.getIrrelevantNodes(this.getUID());
//        newMetadata.getStorageNodes().size();
//        System.out.printf("Size of nodes needed to be added : %d\n",irrelevantNodes.size());
//        for(KVStorageNode node : irrelevantNodes){
//            System.out.printf("Node will be added: %s\n",node.toJSONObject().toString());
//        }
        // Add irrelevant nodes into the metadata
        metadataController.addStorageNodes(newMetadata.getIrrelevantNodes(this.getUID()));
        //System.out.println("Final metadata: \n");
        //metadataController.getMetaData().print();
        // Add relevant nodes additional information into metadata
        lockWrite();
        //metadataController.getMetaData().print();
        migrateData();
        unlockWrite();
    }

    public void handleChangeInCluster(String clusterName, KVStorageNode newReplica) throws Exception{
        KVStorageCluster cluster = (KVStorageCluster)metadataController.getStorageNode(clusterName);
        moveData(cluster.getHashRange(),newReplica);
        cluster.addNode(newReplica);
    }

	public boolean isKeyResponsible(String key, boolean isWrite){
//	    System.out.println(String.format("Upper: %s",metadataController.getStorageNode(getNetworkNode()).getHashRangeString().getUpperBound().toString()));
//	    System.out.println(String.format("Key  : %s",metadataController.hash(key)));
//        System.out.println(String.format("Lower: %s",metadataController.getStorageNode(getNetworkNode()).getHashRangeString().getLowerBound().toString()));
         KVStorageNode node = metadataController.getResponsibleStorageNode(key);
         if(node!=null){
             if(node.getNodeType() == eKVNetworkNodeType.STORAGE_CLUSTER){
                KVStorageCluster cluster = (KVStorageCluster) node;
                if(cluster.contain(this.getUID())){
                    if(isWrite){
                        return cluster.getPrimaryNode().getUID().matches(this.getUID());
                    }
                    else{
                        return true;
                    }
                }
                else{
                    return false;
                }
             }
             else{
                 boolean ret = node.getUID().matches(this.getUID());
                 return ret;
             }
         }
         else{
             System.out.println(String.format("Node is null, take whatever I can, expect: %s",config.getServerName()));
             return true;
         }
//        System.out.println(String.format("In range: %b",ret));
//        System.out.println("-------------------------------------------------");
    }

    public KVStorageNode getResponsibleNode(String key){
	    return this.metadataController.getResponsibleStorageNode(key);
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

    public void printResponsibleKeyValuePair() throws Exception {
	    Set<String> keys = database.getKeys();
	    for(String key: keys){
	        if(isKeyResponsible(key,false)){
	            System.out.printf("%s,%s\n",key, database.getKV(key));
            }
        }
    }

    public void setNode(KVStorageNode node) {
        this.node = node;
    }

    public KVStorageNode getStorageNode() {
        return node;
    }

    public KVMetadataController getMetadataController() {
        return metadataController;
    }

    public boolean joinCluster(String clusterpath){
        KVStorageCluster cluster =null;
        try {
             cluster = zkClient.joinCluster(clusterpath);
        } catch (Exception e){
            kv_out.println_error("fail to join cluster");
        }
        if(cluster!=null){
            this.metadataController.addStorageNode(cluster);
            //this.metadataController.getMetaData().print();
            if(cluster.getPrimaryNode().getUID().matches(this.getUID())){
                // primary node, need to declare victory
                this.clusterCommunicationModule.announcePrimary(this.getUID(),cluster.getUID());
                this.clusterCommunicationModule.startUpdateDaemon();
            }
            else{
                System.out.printf("Server %s join cluster %s, primary is %s\n", this.getUID(),clusterpath,cluster.getPrimaryNodeUID());
            }
            return true;
        }
        return false;
    }

    public boolean exitCluster(String clusterPath){
	    String clusterName = zkClient.getClusterPathFromPath(clusterPath);
        KVStorageCluster cluster = (KVStorageCluster) this.metadataController.getStorageNode(clusterName);
        if(cluster!=null){
            cluster.removeNodeByUID(clusterName);
            try {
                zkClient.exitCluster(clusterPath);
                return true;
            } catch (Exception e) {
                kv_out.println_error("Fail to exit cluster");
                return false;
            }
        }
        return false;
    }

    public KVClusterCommunicationModule getClusterCommunicationModule() {
        return clusterCommunicationModule;
    }

    public KVMigrationModule getMigrationModule() {
        return migrationModule;
    }
}
