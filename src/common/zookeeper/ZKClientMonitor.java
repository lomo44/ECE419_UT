package common.zookeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException.*;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.Watcher.Event.EventType;

import common.metadata.KVMetadataController;
import common.networknode.KVStorageNode;

public class ZKClientMonitor {
	private ZKClient client;
	
	public ZKClientMonitor(ZKClient C) {
		client=C;
	}
	
	
	private Watcher TaskStateMonitor = new Watcher() {
		public void process(WatchedEvent e) {
			if(e.getType() == EventType.NodeChildrenChanged) {
				MonitorTask(e.getPath());
			}
		}
	};
	
	private ChildrenCallback TaskMonitorCallback = new ChildrenCallback() {
		public void processResult(int rc, String path, Object ctx,List<String> children) {
			switch (Code.get(rc)) {
	        case CONNECTIONLOSS:
	        		MonitorTask(path);
	            break;
	        case OK:
	        		((ZKClient)ctx).TaskQueue = children;
	        		System.out.println("Task " + children.toString() + " found");
	            break;
	        case NONODE:
	        		System.out.println("Task node: " + path + " not found");
	            break;
	        default:
	        		System.out.println("Error while getting Tasks");
	        }
		}
	};
	
	public void MonitorTask(String path) {
		client.zk.getChildren(path,
				TaskStateMonitor,
				TaskMonitorCallback,
				client);	
		}
	
	private Watcher MetadataWatcher = new Watcher() {
		public void process(WatchedEvent e) {
			if(e.getType() == EventType.NodeDataChanged) {
				MonitorMetaData(e.getPath());
			}
		}
	};
	
	public void MonitorMetaData(String path) {
		byte[] metadata = client.DataHandler.getDataSync(path,MetadataWatcher);
		try {
			client.updataMetaData(metadata);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
	
