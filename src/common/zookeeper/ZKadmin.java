package common.zookeeper;

import logger.KVOut;

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

import common.messages.KVJSONMessage;
import common.metadata.KVMetadata;
import common.metadata.KVMetadataController;
import common.networknode.KVStorageNode;



public class ZKadmin extends ZKInstance {

	protected List<String> ActiveServer = new ArrayList<String>();
	protected Set<String> PendingTask = new HashSet<String>(); 
	protected Map<String,String[]> config;
	private 	ZKAdminMonitor serverMonitorHandler = new ZKAdminMonitor(this);
	

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
		
	public List<KVStorageNode> addNodes(int count,List<String> serverstoAdd) {
		 serverstoAdd.removeAll(ActiveServer);
		 Collections.shuffle(serverstoAdd);
		 serverstoAdd = serverstoAdd.subList(0, count);
		 serverstoAdd.addAll(ActiveServer);
		 List<KVStorageNode> finallistOfServers = new ArrayList<KVStorageNode>();
		 for (String server: serverstoAdd) {
			 String[] value= config.get(server);
			 KVStorageNode node = new KVStorageNode(value[0],Integer.parseInt(value[1]));
			 finallistOfServers.add(node);
		 }
		 return finallistOfServers;
	}
	
	private byte[] genMetadata(List<KVStorageNode> nodes) {
		Metadatacontroller.createMetaData(nodes);
		return Metadatacontroller.getMetaData()
								.toKVJSONMessage()
								.MetaDatatoBytes();
	}
	
	public void setupServer() {
		init();
		serverMonitorHandler.monitorServers(SERVEROOT);
	}
	
	@Override
	protected void init() {
		try {
			List<String> childServers=zk.getChildren(SERVEROOT,false);
			System.out.println("server root found, active servers #: " + childServers.size());
			createNodeHandler.createNodeSync(METADATA,"",0);
			
		} catch (KeeperException e) {
			switch (e.code()){
			case CONNECTIONLOSS:
				init();
				break;
			case NONODE:
        			System.out.println("No Servers found, creating server root: " + SERVEROOT);
				createNodeHandler.createNodeSync(SERVEROOT,"",0);
				break;
			default:
        			System.out.println("Error while init server root " + SERVEROOT);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<KVStorageNode> updateMetadata(int count) {
		List<KVStorageNode> nodes = addNodes(count,new ArrayList<String>(config.keySet()));
		byte[] metadata = genMetadata(nodes);
		DataHandler.setDataSync(METADATA, metadata,-1);
		byte[] output = DataHandler.getDataSync(METADATA);
		assert output==metadata;
		return nodes;
	}

	public ArrayList<String> getConfig() {
	    return new ArrayList<String>(config.keySet());
    }
	
}


