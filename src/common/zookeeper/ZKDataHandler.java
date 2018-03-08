package common.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZKDataHandler {
	private ZooKeeper zk ;

	public ZKDataHandler(ZooKeeper ZK) {
		zk=ZK;
	}
	
	public void setDataSync(String path, byte[] content, int version) {
		try {
			zk.setData(path, content, version);
			System.out.println("Data " + content.toString() + "set successfully");
		} catch (KeeperException e) {
			switch(e.code()) {
			case CONNECTIONLOSS:
				setDataSync(path, content, version);
				break;
	        default:
        			System.out.println(e.getMessage());
        			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] getDataSync(String path) {
		try {
			 byte[] output=zk.getData(path, false, null);
			 return output;
		} catch (KeeperException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return null;
	}
	
	public byte[] getDataSync(String path, Watcher watcher) {
		try {
			 byte[] output=zk.getData(path, watcher, null);
			 return output;
		} catch (KeeperException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
