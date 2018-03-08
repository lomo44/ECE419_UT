package common.zookeeper;

import java.util.ArrayList;
import java.util.List;

import common.messages.KVJSONMessage;
import common.metadata.KVMetadata;
import logger.KVOut;

public class ZKClient extends ZKInstance{
	
	private static String START = "start";
	private static String STOP = "stop";
	private String serverPath;
	private ZKClientMonitor ClientMonitorHandler= new ZKClientMonitor(this);
	protected List<String> TaskQueue = new ArrayList<String>();
	
	public ZKClient(String hostPort, KVOut logger, String servername) {
		super(hostPort, logger);
		serverPath = "/" + servername;
	}

	protected void updataMetaData(byte[] metadata) {
		KVJSONMessage temp = new KVJSONMessage();
		temp.MetadatafromBytes(metadata, 0, metadata.length);
		Metadatacontroller.putMetaData(KVMetadata.fromKVJSONMessage(temp)); 
	}

	@Override
	protected void init() {
		createNodeHandler.createNodeSync(serverPath, "", 1);
		ClientMonitorHandler.MonitorMetaData(METADATA);
		ClientMonitorHandler.MonitorTask(serverPath);
	}
}
