package common.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

public class ZKmodifyData {
	private ZKadmin client;

	public ZKmodifyData(ZKadmin ZK) {
		client=ZK;
	}
	
	public void setDataSync(String path, byte[] content, int version) {
		try {
			client.zk.setData(path, content, version);
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
			 byte[] output=client.zk.getData(path, false, null);
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
