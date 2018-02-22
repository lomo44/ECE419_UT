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



public class ZKWatcher implements Watcher{

	
	protected ZooKeeper zk;
	private String hostPort;
	final CountDownLatch connection = new CountDownLatch(1);
	private KVOut kv_out;
	protected List<String> ActiveServer = new ArrayList<String>();
	protected Map<String,String[]> config;
	protected KVMetadataController Metadatacontroller = new KVMetadataController();

	public ZKWatcher(String hostPort,KVOut logger, Map<String,String[]> configuration) {
		this.hostPort=hostPort;
		this.kv_out=logger;
		this.config=configuration;
	}
	
	public void connect() {
		try {
			zk = new ZooKeeper(hostPort,15000,this);
		    connection.await();
		} catch (IOException | InterruptedException e) {
			System.out.println("Error occurs when connecting to ZK server");
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			zk.close();
		} catch (InterruptedException e) {
			System.out.println("Error occurs when disconnecting ZK server");
			e.printStackTrace();
		}
	}
	
	private void executeJob(Runnable R) {
		Thread T = new Thread(R);
		T.start();
	}
	
	/*
	 * mode				0:PERSISTENT
	 * 				    1:EPHEMERAL
	 * 					2:PERSISTENT_SEQUENTIAL
	 * 					3.EPHEMERAL_SEQUENTIAL
	 */
	public Thread createNode(String nodename , int mode, String content) {
		ZKcreateNode handler = new ZKcreateNode(nodename,mode,content,zk);
		Thread T = new Thread(handler);
		return T;
	}
		
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
	
	public void initServerMetaData(String childServerDir, String metaDatadir) {
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
	
	public void setupServer() {
		ZKmonitorServers handler = new ZKmonitorServers(this);
		initServerMetaData("/servers","/metadata");
		handler.monitorServers("/servers");
	}
	
	public void setupNodes(int count) {
		List<KVStorageNode> nodes = addNodes(count,new ArrayList<String>(config.keySet()));
		byte[] metadata = genMetadata(nodes);
		ZKmodifyData ModifyDataHandle = new ZKmodifyData(this);
		ModifyDataHandle.setDataSync("/metadata", metadata,-1);
		byte[] output = ModifyDataHandle.getDataSync("/metadata");
		assert output==metadata;
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.println(event);
		if (event.getState()==KeeperState.SyncConnected) {
			connection.countDown();
		}
	}
}


