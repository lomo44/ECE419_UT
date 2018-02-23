package common.zookeeper;

import logger.KVOut;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.MemoryHandler;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import common.metadata.KVMetadata;
import common.metadata.KVMetadataController;
import common.networknode.KVStorageNode;



public class ZKadmin extends ZKInstance {

	protected List<String> ActiveServer = new ArrayList<String>();
	protected Map<String,String[]> config;

	public ZKadmin(String hostPort,KVOut logger, Map<String,String[]> configuration) {
		super(hostPort,logger);
		this.config= configuration;
	}
	
	public List<String> getActiveServers(){
		return ActiveServer;
	}
	
//	private void executeJob(Runnable R) {
//		Thread T = new Thread(R);
//		T.start();
//	}
		
	public List<KVStorageNode> addNodes(int count,List<String> serverstoAdd) {
		 serverstoAdd.removeAll(ActiveServer);
		 Collections.shuffle(serverstoAdd);
		 serverstoAdd = serverstoAdd.subList(0, count);
		 serverstoAdd.addAll(ActiveServer);
		 List<KVStorageNode> storageNodes = new ArrayList<KVStorageNode>();
		 for (String server: serverstoAdd) {
			 String[] value= config.get(server);
			 KVStorageNode node = new KVStorageNode(value[0],Integer.parseInt(value[1]));
			 storageNodes.add(node);
		 }
		 return storageNodes;
	}
	
	private byte[] genMetadata(List<KVStorageNode> nodes) {
		Metadatacontroller.createMetaData(nodes);
		return Metadatacontroller.getMetaData()
								.toKVJSONMessage()
								.MetaDatatoBytes();
	}
	
	public void setupServer() {
		ZKmonitorServers handler = new ZKmonitorServers(this);
		initServerMetaData("/servers","/metadata");
		handler.monitorServers("/servers");
	}
	
	private void initServerMetaData(String childServerDir, String metaDatadir) {
		try {
			List<String> childServers=zk.getChildren(childServerDir,false);
			System.out.println("Metadata found, active servers #: " + childServers.size());
		} catch (KeeperException e) {
			switch (e.code()){
			case CONNECTIONLOSS:
				initServerMetaData(childServerDir,metaDatadir);
				break;
			case NONODE:
        			System.out.println("No Metadata found, creating Metadata: " + childServerDir);
				ZKcreateNode handler = new ZKcreateNode(childServerDir,0,"",zk);
				handler.createNodeSync(childServerDir,"",0);
				break;
			default:
        			System.out.println("Error while init Metadata " + childServerDir);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ZKcreateNode handler = new ZKcreateNode(metaDatadir,0,"",zk);
		handler.createNodeSync("/metadata","",0);
	}
	
	public List<KVStorageNode> setupNodes(int count) {
		List<KVStorageNode> nodes = addNodes(count,new ArrayList<String>(config.keySet()));
		byte[] metadata = genMetadata(nodes);
		ZKmodifyData ModifyDataHandle = new ZKmodifyData(this);
		ModifyDataHandle.setDataSync("/metadata", metadata,-1);
		byte[] output = ModifyDataHandle.getDataSync("/metadata");
		assert output==metadata;
		return nodes;
	}
	
}


