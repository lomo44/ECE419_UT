package common.zookeeper;

import app_kvServer.KVServer;
import app_kvServer.KVServerConfig;
import common.messages.KVJSONMessage;
import common.metadata.KVMetadata;
import common.networknode.KVStorageCluster;
import common.networknode.KVStorageNode;

import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ZKClient extends ZKInstance{

	private String serverPath;
	private String serverConfigPath;
	private String serverMetadataPath;
	private String serverTaskQueuePath;
	private String serverPoolPath;
	private static final Pattern regex_sequenceNumber = Pattern.compile("n_(\\d*)");

	//private ZKClientMonitor ClientMonitorHandler= new ZKClientMonitor(this);
	private KVServer serverInstance;
	private KVServerConfig serverConfig;

	private Watcher metadataWatcher = new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			if(event.getType() == Event.EventType.NodeDataChanged){
				try {
					System.out.println("Handle Metadata Change");
					serverInstance.handleChangeInMetadata(obtainMetadataFromZK());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * cluster watcher for primary. Need to keep track of current nodes under cluster/clustername/newreplicas
	 */
	private Watcher clusterWatcher = new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			switch (event.getType()){
				case NodeChildrenChanged:{
					// Underlying node change
					try {
						migrateNewReplicas(event.getPath());
					} catch (Exception e) {
					 	e.printStackTrace();
					}
					break;
				}
			}
		}
	};
	/**
	 * This watcher is used for watching precedent node, if the precedent node is down, then it becomes the primary.
	 */
	private Watcher primaryWatcher = new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			switch (event.getType()){
				case NodeDeleted:{
					// proceding node got deleted, reevalutate the leader situation
					String clusterpath = getClusterPathFromPath(event.getPath());
					try {
						createClusterInstance(clusterpath);
					} catch (Exception e){
					}
				}
			}
		}
	};

	public ZKClient(String connectionString, String servername, KVServer serverInstance) throws KeeperException, InterruptedException {
		super(connectionString, serverInstance.getLogger());
		serverPath = SERVER_BASE_PATH +"/"+servername;
		serverConfigPath = serverPath+"/"+SERVER_CONFIG_NAME;
		serverMetadataPath = serverPath+"/"+SERVER_METADATA_NAME;
		serverTaskQueuePath = serverPath + "/" + SERVER_TASK_QUEUE_NAME;
		serverPoolPath =  SERVER_POOL_BASE_PATH + "/"+servername;
		this.serverInstance = serverInstance;

		System.out.println("Try to initialize server");
		// Retain server configuration file
		KVJSONMessage msg_config = new KVJSONMessage();
		byte[] config_data = zk.getData(serverConfigPath,false,null);
		System.out.println(String.format("Loading configuration file from: %s",serverConfigPath));
		msg_config.fromBytes(config_data,0,config_data.length);
		serverConfig =  KVServerConfig.fromKVJSONMessage(msg_config);
		System.out.println("Try to initialize metadata");
		// Retain server initial metadata file;
		obtainMetadataFromZK();
		serverInstance.initializeServer(serverConfig, null,this);
		signalInitialization();
	}

	protected void updataMetaData(byte[] metadata) throws Exception {
		KVJSONMessage temp = new KVJSONMessage();
		temp.MetadatafromBytes(metadata, 0, metadata.length);
		serverInstance.handleChangeInMetadata(KVMetadata.fromKVJSONMessage(temp));
	}

	/**
	 * join a cluster based on cluster path. This function will not initialize any server part
	 * @param clusterName zookeeper cluster path
	 * @return KVStorageCluster if joining is successfull
	 * @throws Exception 
	 */
	public KVStorageCluster joinCluster(String clusterName) throws Exception {
		// Create a node on the cluster path;
		zk.create(getOldReplciaPath(clusterName)+"/n_",
				serverConfig.toKVJSONMessage().toBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
		KVStorageCluster cluster = createClusterInstance(clusterName);
		if(cluster.getPrimaryNode().getUID().matches(this.serverInstance.getUID())){
			// setup watch for the cluster;
			System.out.printf("Server: %s is selected as leader of cluster: %s\n",serverInstance.getUID(),clusterName);
			migrateNewReplicas(getNewReplicasPath(clusterName));
		}
		else {
            zk.create(getNewReplicasPath(clusterName) + "/n_",
                    serverConfig.toKVJSONMessage().toBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        }
		return cluster;
	}
	
	
	// primary server migrate data to pending new replica, handles case where leader not available yet
	private synchronized void migrateNewReplicas(String path) throws Exception {
		List<String> children = zk.getChildren(path,clusterWatcher);
		if (!children.isEmpty()) {
			for (String name : children) {
				String subPath=path + "/" + name;
				KVServerConfig config = getServerConfigFromPath(subPath);
				serverInstance.handleChangeInCluster(getClusterPathFromPath(path),new KVStorageNode(config));
				zk.delete(subPath, -1);
			}
		}
	}

	/**
	 * Exit a cluster. This function assume that the caller has remove the local node from the cluster
	 * @param clusterName
	 */
	public void exitCluster(String clusterName) throws KeeperException, InterruptedException {
		// remove local node from local metadata
        String path = getOldReplciaPath(clusterName);
		List<String> children = zk.getChildren(path,false);
		for(String child: children){
			KVServerConfig config = getServerConfigFromPath(path+"/"+child);
			if(config.getServerName().matches(this.serverInstance.getUID())){
				//server node find, remove the node
				zk.delete(path+"/"+child,-1);
			}
		}
	}

	private KVStorageCluster createClusterInstance(String clusterName) throws KeeperException, InterruptedException {
		String path = getOldReplciaPath(clusterName);
		List<String> sortedChildren = zk.getChildren(path,false);
		Collections.sort(sortedChildren);
		KVServerConfig primaryConfig = getServerConfigFromPath(path+"/"+sortedChildren.get(0));
		if(primaryConfig.getServerName() != this.serverConfig.getServerName()){
			setupPrecedingPrimary(path,sortedChildren);
		}
		List<KVServerConfig> memberConfig = getServerConfigsFromClusterName(clusterName,sortedChildren);
		return new KVStorageCluster(clusterName,primaryConfig,memberConfig);
	}

	private KVServerConfig getServerConfigFromPath(String clusterPath) throws KeeperException, InterruptedException {
		KVJSONMessage msg = new KVJSONMessage();
		byte[] data = zk.getData(clusterPath,false,null);
		msg.fromBytes(data,0,data.length);
		return KVServerConfig.fromKVJSONMessage(msg);
	}

	private List<KVServerConfig> getServerConfigsFromClusterName(String clusterName, List<String> sortedChildren)
			throws KeeperException, InterruptedException {
		List<KVServerConfig> ret = new ArrayList<>();
		for (String child: sortedChildren
			 ) {
			ret.add(getServerConfigFromPath(getOldReplciaPath(clusterName)+"/"+child));
		}
		return ret;
	}

	private void setupPrecedingPrimary( String clusterpaths,List<String> sortedChildren) throws KeeperException, InterruptedException {
		String previous = "";
		for (int i = 0; i < sortedChildren.size(); i++) {
			if(sortedChildren.get(i).matches(this.serverConfig.getServerName())){
				zk.getData(clusterpaths+"/"+previous,primaryWatcher,null);
				break;
			}
			previous = sortedChildren.get(i);
		}
	}

	protected void signalInitialization(){
		createNodeHandler.createNodeSync(serverPoolPath,"I am here",1);
	}

	@Override
	protected void init() {
	}

	private KVMetadata obtainMetadataFromZK() throws KeeperException, InterruptedException {
		KVJSONMessage msg_metadata = new KVJSONMessage();
		System.out.println(String.format("Try to get metadata from: %s",serverMetadataPath));
		byte[] metadata_data = zk.getData(serverMetadataPath, metadataWatcher,null);
		try {
			msg_metadata.fromBytes(metadata_data ,0,metadata_data .length);
		}
		catch (IllegalArgumentException e){
			return null;
		}
		return KVMetadata.fromKVJSONMessage(msg_metadata);
	}
}
