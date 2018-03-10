package common.zookeeper;

import app_kvServer.KVServer;
import app_kvServer.KVServerConfig;
import common.messages.KVJSONMessage;
import common.metadata.KVMetadata;
import org.apache.zookeeper.*;

public class ZKClient extends ZKInstance{

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
		System.out.println(new String(config_data));
		msg_config.fromBytes(config_data,0,config_data.length);
		serverConfig =  KVServerConfig.fromKVJSONMessage(msg_config);
		System.out.println("Try to initialize metadata");
		// Retain server initial metadata file;
		serverInstance.initializeServer(serverConfig, null);
		signalInitialization();
	}

	protected void updataMetaData(byte[] metadata) throws Exception {
		KVJSONMessage temp = new KVJSONMessage();
		temp.MetadatafromBytes(metadata, 0, metadata.length);
		serverInstance.handleChangeInMetadata(KVMetadata.fromKVJSONMessage(temp));
	}

	protected void signalInitialization(){
		createNodeHandler.createNodeSync(serverPoolPath,"I am here",1);
	}

	@Override
	protected void init() {
	}

	private KVMetadata obtainMetadataFromZK() throws KeeperException, InterruptedException {
		KVJSONMessage msg_metadata = new KVJSONMessage();
		byte[] metadata_data = zk.getData(serverMetadataPath, metadataWatcher,null);
		msg_metadata.fromBytes(metadata_data ,0,metadata_data .length);
		return KVMetadata.fromKVJSONMessage(msg_metadata);
	}
}
