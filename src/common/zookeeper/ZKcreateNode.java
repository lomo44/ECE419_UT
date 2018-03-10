package common.zookeeper;



import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.KeeperException.Code;

public class ZKcreateNode implements Runnable{
	
	private String path; 
	private String content;
	private CreateMode mode;
	private ZooKeeper zk;
	boolean finish;
	
	//thread handler
	public ZKcreateNode(String Path, int Mode, String Content, ZooKeeper ZK) {
		path=Path;
		content=Content;
		zk=ZK;
		mode = flagtoCreateMode(Mode);
	}
	
	//normal handler
	public ZKcreateNode(ZKInstance instance) {
		zk=instance.zk;
	}
	
	private CreateMode flagtoCreateMode(int flag) {
		CreateMode mode = null;
		try {
			mode = CreateMode.fromFlag(flag);
		} catch (KeeperException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return mode;
	}

	private StringCallback createNodeThreadCallBack = new StringCallback() {
		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
	        case CONNECTIONLOSS:
	        		 createNodeThread();
	            break;
	        case OK:
	        		System.out.println("Znode " + path + " created");
	        		synchronized(ctx) { ctx.notifyAll(); }
	            break;
	        case NODEEXISTS:
	        		System.out.println("Znode " + path + " already exist");
        			synchronized(ctx) { ctx.notifyAll(); }
        			break;
	        default:
	        		System.out.println("Error while creating Znode " + path);
        			synchronized(ctx) { ctx.notifyAll(); }    
	        	}
		}
	};
	
	private void createNodeThread() {
		zk.create(path,content.getBytes(),
				ZooDefs.Ids.OPEN_ACL_UNSAFE,
				mode,
				createNodeThreadCallBack,
				null);	
	}
	
	@Override
	public void run() {
		createNodeThread();
		synchronized(this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void createNodeAsync(String path, final String content, final int mode) {
		 StringCallback createNodeAsyncCallBack = new StringCallback() {
				public void processResult(int rc, String path, Object ctx, String name) {
					switch (Code.get(rc)) {
			        case CONNECTIONLOSS:
			        		createNodeAsync(path, content, mode);
			            break;
			        case OK:
			        		System.out.println("Znode " + path + " created");
			            break;
			        case NODEEXISTS:
			        		System.out.println("Znode " + path + " already exist");
			        		break;
			        default:
			        		System.out.println("Error while creating Znode " + path);    
			        	}
				}
		 };
				
		zk.create(path,content.getBytes(),
				ZooDefs.Ids.OPEN_ACL_UNSAFE,
				flagtoCreateMode(mode),
				createNodeAsyncCallBack,
				null);	
	}
	
	public void createNodeSync(String path, String content, int mode) {
		try {
			zk.create(path, content.getBytes(), 
					ZooDefs.Ids.OPEN_ACL_UNSAFE, 
					flagtoCreateMode(mode)
					);
			System.out.println("Znode " + path + " created");
		} catch (KeeperException e) {
			switch(e.code()) {
			case CONNECTIONLOSS:
				createNodeSync(path,content,mode);
				break;
	        case NODEEXISTS:
        			System.out.println("Znode " + path + " already exist");
        			break;
	        default:
        			System.out.println("Error while creating Znode " + path);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
