package common.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import logger.KVOut;

public abstract class ZKInstance implements Watcher {
	protected static String METADATA = "/metadata";
	protected static String SERVEROOT = "/servers";
	protected ZooKeeper zk;
	protected String connectionString;
	final CountDownLatch connection = new CountDownLatch(1);
	protected KVOut kv_out;
	protected ZKcreateNode createNodeHandler; 
	protected ZKDataHandler DataHandler;
	public ZKInstance(String connectionString, KVOut logger) {
		this.connectionString =connectionString;
		this.kv_out=logger;
	}
	
	public void connect() {
		try {
			zk = new ZooKeeper(connectionString,15000,this);
		    connection.await();
		    createNodeHandler = new ZKcreateNode(this.zk);
		    DataHandler = new ZKDataHandler(this.zk);
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
	
	protected abstract void init();

	
	@Override
	public void process(WatchedEvent event) {
		System.out.println(event);
		if (event.getState()==KeeperState.SyncConnected) {
			connection.countDown();
		}
	}
}
