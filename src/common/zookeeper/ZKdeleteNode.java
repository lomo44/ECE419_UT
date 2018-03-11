package common.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZKdeleteNode {
	
	private ZooKeeper zk;
	
	public ZKdeleteNode(ZKInstance instance) {
		zk=instance.zk;
	}
	
	public void deleteNodeSync(String path, int version) {
		try {
			zk.delete(path, version);
			System.out.println("Znode " + path + " deleted");
		} catch (KeeperException e) {
			switch(e.code()) {
			case CONNECTIONLOSS:
				deleteNodeSync(path,version);
				break;
	        case NONODE:
        			System.out.println("Znode " + path + " already gone");
        			break;
	        default:
        			System.out.println("Error while removing Znode " + path);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
