package common.zookeeper;

import java.util.ArrayList;
import java.util.List;

import app_kvServer.KVServer;
import common.messages.KVJSONMessage;
import common.metadata.KVMetadata;
import logger.KVOut;

public class ZKClient extends ZKInstance{
	
	private static String START = "start";
	private static String STOP = "stop";
	private String serverPath;
	private ZKClientMonitor ClientMonitorHandler= new ZKClientMonitor(this);
	protected List<String> TaskQueue = new ArrayList<String>();
	private KVServer serverInstance;
	public ZKClient(String hostPort,String servername, KVServer serverInstance) {
		super(hostPort, serverInstance.getLogger());
		serverPath = "/" + servername;
		this.serverInstance = serverInstance;
	}

	protected void updataMetaData(byte[] metadata) throws Exception {
		KVJSONMessage temp = new KVJSONMessage();
		temp.MetadatafromBytes(metadata, 0, metadata.length);
		serverInstance.handleChangeInMetadata(KVMetadata.fromKVJSONMessage(temp));
	}

	@Override
	protected void init() {
		createNodeHandler.createNodeSync(serverPath, "", 1);
		ClientMonitorHandler.MonitorMetaData(METADATA);
		ClientMonitorHandler.MonitorTask(serverPath);
	}
}
