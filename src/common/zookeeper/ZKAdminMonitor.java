package common.zookeeper;

import java.util.List;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;


public class ZKAdminMonitor {
	
	private ZKadmin admin;
	
	public ZKAdminMonitor(ZKadmin C) {
		admin=C;
	}
	
	private ChildrenCallback ServerMonitorCallback = new ChildrenCallback() {
		public void processResult(int rc, String path, Object ctx,List<String> children) {
			switch (Code.get(rc)) {
	        case CONNECTIONLOSS:
	        	monitorServers(path);
	            break;
	        case OK:
	        		admin.updateCurrentSetupNodesName(children);
	        		//System.out.println("Server pool " + path + " found, Count: "+Integer.toString(admin.getCurrentSetupNodesNames().size()));
	            break;
	        case NONODE:
	        		System.out.println("Server poll: " + path + " not found");
	            break;
	        default:
	        		System.out.println("Error while getting server pool");
	        }
		}
	};
	
	
//	private StatCallback TaskCallBack = new StatCallback() {
//		public void processResult(int rc, String path, Object ctx, Stat stat) {
//			switch (Code.get(rc)) {
//	        case CONNECTIONLOSS:
//	        		TaskMonitor(path);
//	            break;
//	        case OK:
//	        		System.out.println("Task " + path + " found");
//	            break;
//	        case NONODE:
//	        		System.out.println("Server Metadata: " + path + " not found");
//	            break;
//	        default:
//	        		System.out.println("Error while getting servers");
//	        }
//		}
//	};
	

//	private Watcher TaskWatcher = new Watcher() {
//		public void process(WatchedEvent e) {
//			if(e.getType() == EventType.NodeDeleted) {
//				admin.PendingTask.remove(e.getPath());
//			}
//		}
//	};
	
	private Watcher ServerStateMonitor = new Watcher() {
		public void process(WatchedEvent e) {
			if(e.getType() == EventType.NodeChildrenChanged) {
				monitorServers(e.getPath());
			}
		}
	};
	
//	public void TaskMonitor(String path) {
//		admin.zk.exists(path,
//				TaskWatcher,
//				TaskCallBack,
//				admin);
//	}
	
	public void monitorServers(String path) {
		admin.zk.getChildren(path,
				ServerStateMonitor,
				ServerMonitorCallback,
				admin);
	}
	
	

}
