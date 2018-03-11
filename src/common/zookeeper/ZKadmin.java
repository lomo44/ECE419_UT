package common.zookeeper;

import common.metadata.KVMetadata;
import logger.KVOut;

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.zookeeper.KeeperException;

import app_kvServer.KVServerConfig;
import common.networknode.KVStorageNode;



public class ZKadmin extends ZKInstance {

//	private List<KVStorageNode> sleepingServer;
//	private List<KVStorageNode> runningServer;

	private Set<String> currentSetupNodes = new ConcurrentHashMap().newKeySet();

	private ZKAdminMonitor serverMonitorHandler = new ZKAdminMonitor(this);


	public ZKadmin(String connectionString,KVOut logger) {
		super(connectionString,logger);
		//this.sleepingServer = idleServer;
		init();
	}

	public void addNodeIndicator(String nodeName){
		this.currentSetupNodes.add(nodeName);
	}

	public Set<String> getCurrentSetupNodesNames() {
		return currentSetupNodes;
	}


	public synchronized void updateCurrentSetupNodesName(List<String> names){
		currentSetupNodes.clear();
		currentSetupNodes.addAll(names);
		System.out.println(String.format("Current Setup Nodes: %d",names.size()));
	}

	public void setupNodes(List<KVStorageNode> nodes, String cacheStrategy, int cacheSize) {
		for (KVStorageNode server : nodes) {
			KVServerConfig config = new KVServerConfig();
			config.setCacheSize(cacheSize);
			config.setCacheStratagy(cacheStrategy);
			config.setServerPort(server.getPortNumber());
			config.setServerHost(server.getHostName());
			String path = SERVER_BASE_PATH + "/" + server.getserverName();
			String metadatapath = path + "/" + SERVER_METADATA_NAME;
			String configpath = path + "/" + SERVER_CONFIG_NAME;
			createNodeHandler.createNodeSync(path, "", 0);
			// We don't need to populate meta data for now since we are not doing migration
			createNodeHandler.createNodeSync(metadatapath, "", 0);
			createNodeHandler.createNodeSync(configpath,new String(config.toKVJSONMessage().toBytes()),1);
		}
	}


	public void removeNodes(List<KVStorageNode> nodes) throws KeeperException, InterruptedException {
		for (KVStorageNode server : nodes) {
			removeNodes(server);
		}
	}


	public void removeNodes(KVStorageNode nodes) throws KeeperException, InterruptedException {
		String path = SERVER_BASE_PATH + "/" + nodes.getserverName();
		String metadatapath = path + "/" + SERVER_METADATA_NAME;
		String configpath = path + "/" + SERVER_CONFIG_NAME;
		zk.delete(path,-1);
	}

	public void broadcastMetadata(List<KVStorageNode> nodes, KVMetadata metadata){
		for (KVStorageNode node: nodes
				) {
			String path = SERVER_BASE_PATH + "/" + node.getserverName();
			String metadatapath = path + "/" + SERVER_METADATA_NAME;
			DataHandler.setDataSync(metadatapath,metadata.toKVJSONMessage().toBytes(),0);
		}
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
		serverMonitorHandler.monitorServers(SERVER_POOL_BASE_PATH);
	}

	public void close() throws KeeperException, InterruptedException {
		deleteAll(SERVER_BASE_PATH);
		deleteAll(SERVER_POOL_BASE_PATH);
	}

	private void deleteAll(String path) throws KeeperException, InterruptedException {
		List<String> children = zk.getChildren(path,false,null);
		if(children.size()==0){
			zk.delete(path,-1);
		}
		else{
			for (String child: children
					) {
				deleteAll(path+"/"+child);
			}
		}
	}
}


