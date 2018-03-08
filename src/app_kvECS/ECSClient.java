package app_kvECS;

import common.zookeeper.*;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import ecs.IECSNode;
import logger.KVOut;

/*
 * mode				0:PERSISTENT
 * 				    1:EPHEMERAL
 * 					2:PERSISTENT_SEQUENTIAL
 * 					3.EPHEMERAL_SEQUENTIAL
 */


public class ECSClient implements IECSClient {
    
    private static KVOut kv_out = new KVOut("ECS");
    private ZKadmin zkClient;
    private Collection<String> FutureActiveServerSnapShot;
	public ECSClient(String configFile){
		try {
			zkClient= new ZKadmin("localhost:2181",kv_out,importConfig(configFile));
		} catch (IOException e) {
			System.out.print("Error while importing config");
			e.printStackTrace();
		}
	}
    @Override
    public boolean start() {
        // TODO
        return false;
    }

    @Override
    public boolean stop() {
        // TODO
        return false;
    }

    @Override
    public boolean shutdown() {
        // TODO
        return false;
    }

    @Override
    public IECSNode addNode(String cacheStrategy, int cacheSize) {
        // TODO
        return null;
    }

    @Override
    public Collection<IECSNode> addNodes(int count, String cacheStrategy, int cacheSize) {
    		
    		return null;
    }

    @Override
    public Collection<IECSNode> setupNodes(int count, String cacheStrategy, int cacheSize) {
    		zkClient.updateMetadata(count);
        return null;
    }

    @Override
    public boolean awaitNodes(int count, int timeout) throws Exception {
	    	ExecutorService await = Executors.newSingleThreadExecutor();
	        Callable<Boolean> checkawait = new Callable<Boolean>() {
	            @Override
	            public Boolean call() {
	             	boolean result=false;
	            		while(FutureActiveServerSnapShot.containsAll(zkClient.getActiveServers())) {
						List<String> activeServer =new ArrayList<String>(zkClient.getActiveServers());
						Collections.sort(activeServer);
						if (FutureActiveServerSnapShot.equals(activeServer)) {
							result=true;
							break;
							}
	            		}
	    	            return result;
	            }
	        };
	        Future<Boolean> result = await.submit(checkawait);
	        return result.get(timeout, TimeUnit.SECONDS);
    }

    @Override
    public boolean removeNodes(Collection<String> nodeNames) {
        // TODO
        return false;
    }

    @Override
    public Map<String, IECSNode> getNodes() {
        // TODO
        return null;
    }

    @Override
    public IECSNode getNodeByKey(String Key) {
        // TODO
        return null;
    }
    
    
    private Map<String, String[]> importConfig(String file) throws IOException {
    		
    		Map <String,String[]> configInMem= new HashMap <String,String[]>();
    		FileReader config = new FileReader(file);
		BufferedReader readbuf = new BufferedReader(config);
        String fileRead = readbuf.readLine();
        while(fileRead!=null) {
        		String[] splited = fileRead.split("\\s+");
        		configInMem.put(splited[0],Arrays.copyOfRange(splited,1,3));
        		fileRead=readbuf.readLine();
        }
        readbuf.close();
        
        /* testprint config map
        for (Map.Entry<String, String[]> entry : configInMem.entrySet()) {
            System.out.println(entry.getKey()+" : "+Arrays.toString(entry.getValue()));
        }   
        */
        
        return configInMem;
    }
    
    private void runServer(String key,String host, 
    						  String path, String port, 
    						  String CacheSize,  String replaceStrat) throws IOException, InterruptedException 
    {
    		String[] args = new String[] { "ssh", "-i", key, 
    									  "-n", host, "nohup",
    									  "java","-jar",path,port,CacheSize,replaceStrat,"</dev/null &>/dev/null &"};

    		System.out.println("start init server...");
    		Process proc = new ProcessBuilder(args).start();
        BufferedReader reader =  
              new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = "";
        while((line = reader.readLine()) != null) {
            System.out.print(line + "\n");
        }
       int exitcode = proc.waitFor();
       if (exitcode != 0) {
           throw new IOException("Server init failed " + exitcode);
       }
    }   
    
    public static void main(String[] args) {
        kv_out.enableLog("logs/ECS.log", Level.ALL);
        ECSClient admin = new ECSClient(args[0]);
    		ZKadmin zkClient = admin.zkClient;
    		zkClient.connect();
    		zkClient.setupServer();
    		zkClient.updateMetadata(3);
//    		try {
//				admin.runServer("id_rsa",
//						  "nintengao@192.168.2.10",
//						  "~/ECE419_UT/m2-server.jar",
//						  "50000","10","FIFO");
//			} catch (IOException | InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//    		try {
//				Thread.sleep(600000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
    		zkClient.disconnect();
    }
}



