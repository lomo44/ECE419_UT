package common.zookeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException.*;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.Watcher.Event.EventType;

import common.metadata.KVMetadataController;
import common.networknode.KVStorageNode;


public class ZKmonitorServers {
	
	private ZKWatcher client;

	public ZKmonitorServers(ZKWatcher C) {
		client=C;
	}

	
	private ChildrenCallback ServerMonitorCallback = new ChildrenCallback() {
		public void processResult(int rc, String path, Object ctx,List<String> children) {
			switch (Code.get(rc)) {
	        case CONNECTIONLOSS:
	        	monitorServers(path);
	            break;
	        case OK:
	        		client.ActiveServer = children;
	        		System.out.println("Metadata found, active servers:" + client.ActiveServer.size());
	            break;
	        case NONODE:
	        		System.out.println("No Metadata found");
	            break;
	        default:
	        		System.out.println("Error while setting up Metadata " + path);
	        }
		}
	};
	
	private Watcher ServerStateMonitor = new Watcher() {
		public void process(WatchedEvent e) {
			if(e.getType() == EventType.NodeChildrenChanged) { 	
				monitorServers(e.getPath());
			}
		}
	};

	public void monitorServers(String path) {
		client.zk.getChildren(path,
				ServerStateMonitor,
				ServerMonitorCallback,
				client);
	}
	
}
