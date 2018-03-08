package common.zookeeper;

import app_kvServer.KVServer;
import app_kvServer.KVServerConfig;
import common.messages.KVJSONMessage;
import common.metadata.KVMetadata;
import org.apache.zookeeper.*;

public class ZKClient extends ZKInstance{
	private final static String SERVER_BASE_PATH = "/server";
	private final static String SERVER_POOL_BASE_PATH = "/pool";
	private final static String SERVER_CONFIG_NAME = "config";
	private final static String SERVER_METADATA_NAME = "metadata";
	private final static String SERVER_TASK_QUEUE_NAME = "taskQueue";

	private String serverPath;
	private String serverConfigPath;
	private String serverMetadataPath;
	private String serverTaskQueuePath;
	private String serverPoolPath;

	//private ZKClientMonitor ClientMonitorHandler= new ZKClientMonitor(this);
	private KVServer serverInstance;
	private KVServerConfig serverConfig;

	private Watcher metadataWatcher = new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			if(event.getType() == Event.EventType.NodeDataChanged){
				try {
					serverInstance.handleChangeInMetadata(obtainMetadataFromZK());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	public ZKClient(String connectionString, String servername, KVServer serverInstance) throws KeeperException, InterruptedException {
		super(connectionString, serverInstance.getLogger());
		serverPath = SERVER_BASE_PATH + servername;
		serverConfigPath = serverPath+"/"+SERVER_CONFIG_NAME;
		serverMetadataPath = serverPath+"/"+SERVER_METADATA_NAME;
		serverTaskQueuePath = serverPath + "/" + SERVER_TASK_QUEUE_NAME;
		serverPoolPath =  SERVER_POOL_BASE_PATH + "/"+servername;
		this.serverInstance = serverInstance;

		//init();
		connect();

		// Retain server configuration file
		KVJSONMessage msg_config = new KVJSONMessage();
		byte[] config_data = zk.getData(serverConfigPath,false,null);
		msg_config.fromBytes(config_data,0,config_data.length);
		serverConfig =  KVServerConfig.fromKVJSONMessage(msg_config);

		// Retain server initial metadata file;
		serverInstance.initializeServer(serverConfig, obtainMetadataFromZK());
		signalInitialization();
	}

	protected void updataMetaData(byte[] metadata) throws Exception {
		KVJSONMessage temp = new KVJSONMessage();
		temp.MetadatafromBytes(metadata, 0, metadata.length);
		serverInstance.handleChangeInMetadata(KVMetadata.fromKVJSONMessage(temp));
	}

	protected void signalInitialization() throws KeeperException, InterruptedException {
		//createNodeHandler.createNodeSync(serverPoolPath,"I am here",1);
		zk.create(serverPoolPath,"I Am Here".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	}

	@Override
	protected void init() {
		//createNodeHandler.createNodeSync(serverPath, "", 1);
		//ClientMonitorHandler.MonitorMetaData(METADATA);
		//ClientMonitorHandler.MonitorTask(serverPath);
	}

	private KVMetadata obtainMetadataFromZK() throws KeeperException, InterruptedException {
		KVJSONMessage msg_metadata = new KVJSONMessage();
		byte[] metadata_data = zk.getData(serverMetadataPath, metadataWatcher,null);
		msg_metadata.fromBytes(metadata_data ,0,metadata_data .length);
		return KVMetadata.fromKVJSONMessage(msg_metadata);
	}
}
