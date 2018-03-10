package common.zookeeper;

import logger.KVOut;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.MemoryHandler;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import app_kvServer.KVServerConfig;
import common.messages.KVJSONMessage;
import common.metadata.KVMetadata;
import common.metadata.KVMetadataController;
import common.networknode.KVStorageNode;



public class ZKadmin extends ZKInstance {

	protected List<String> ActiveServer = new ArrayList<String>();
	protected List<String> PendingServer = new ArrayList<String>();
	protected Set<String> PendingTask = new HashSet<String>(); 
	protected Map<String,String[]> config;
	private   ZKAdminMonitor serverMonitorHandler = new ZKAdminMonitor(this);
	private KVMetadataController metadataController = new KVMetadataController();

	public ZKadmin(String hostPort,KVOut logger, Map<String,String[]> configuration) {
		super(hostPort,logger);
		this.config=configuration;
	}
	
	public List<String> getActiveServers(){
		return ActiveServer;
	}

//	private void executeJob(Runnable R) {
//		Thread T = new Thread(R);
//		T.start();
//	}

	
	//list of sever kvstoragenode to list of server string names
	public List<KVStorageNode> toKVStorageNodeList(List<String> serverlist){
		List<KVStorageNode> nodelist = new ArrayList<KVStorageNode>();
		for (String server: serverlist) {
			 String[] value= config.get(server);
			 KVStorageNode node = new KVStorageNode(value[0],Integer.parseInt(value[1]),server);
			 nodelist.add(node);
		}
		return nodelist;
	}
	
	//random pick 'count' servers from idle server pool
	public List<String> selectServerToAdd(int count) {
		List<String> serverlist = new ArrayList<String>(config.keySet()); 
		serverlist.removeAll(ActiveServer);
		Collections.shuffle(serverlist);
		serverlist = serverlist.subList(0, count);
		PendingServer = serverlist;
		return serverlist;
	}
	
	//create a snap shot of list of servers that 
	//assemble will look like after adding the servers
	public List<String> createFutureSnapShot(List<String> serverstoAdd){
		List<String> futureSnapShot = new ArrayList<String>(serverstoAdd);
		futureSnapShot.addAll(ActiveServer);
		return futureSnapShot;
	}
	
	
	public void initZKNodes( List<String> serverstoAdd ,String cacheStrategy, int cacheSize) {
		byte[] strat = serverConfigtoByte(cacheStrategy,cacheSize);
		byte[] metadata = metaDataToByte();
		for (String server : serverstoAdd) {
			String path = SERVER_BASE_PATH + "/" + server;
			String metadatapath = path + "/" + SERVER_METADATA_NAME;
			String configpath = path + "/" + SERVER_CONFIG_NAME;
			createNodeHandler.createNodeSync(path, "", 0);
			createNodeHandler.createNodeSync(metadatapath, new String(metadata), 0);
			createNodeHandler.createNodeSync(configpath,new String(strat),0);
		}
	}
	
	private byte[] serverConfigtoByte(String cacheStrategy, int cacheSize) {
		KVServerConfig serverconfig = new KVServerConfig();
		serverconfig.setKeyCacheStratagy(cacheStrategy);
		serverconfig.setCacheSize(cacheSize);
		return serverconfig.toKVJSONMessage().toBytes();
	}
	
	private byte[] metaDataToByte() {
		return metadataController.getMetaData()
								.toKVJSONMessage().toBytes();
	}
	
	public KVMetadata getmetaData() {
		return metadataController.getMetaData();
	}
	
	public void updateMetadata(List<KVStorageNode> servertoAdd) {
		metadataController.clearStorageNodes();
		metadataController.addStorageNodes(servertoAdd);
	}
	
	public void setupServer() {
		init();
		serverMonitorHandler.monitorServers(SERVER_POOL_BASE_PATH);
	}
	
	@Override
	protected void init() {
		try {
			List<String> childServers=zk.getChildren(SERVER_POOL_BASE_PATH,false);
			System.out.println("server root found, active servers #: " + childServers.size());
		} catch (KeeperException e) {
			switch (e.code()){
			case CONNECTIONLOSS:
				init();
				break;
			case NONODE:
        			System.out.println("No Servers found, creating server root: " + SERVER_POOL_BASE_PATH);
				createNodeHandler.createNodeSync(SERVER_POOL_BASE_PATH,"",0);
				break;
			default:
        			System.out.println("Error while init server root " + SERVER_POOL_BASE_PATH);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createNodeHandler.createNodeSync(SERVER_BASE_PATH,"",0);
	}
	

	
}


