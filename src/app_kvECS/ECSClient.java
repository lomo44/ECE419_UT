package app_kvECS;

import common.zookeeper.*;
import java.util.Map;
import org.apache.log4j.Level;
import java.util.Collection;
import java.util.HashMap;
import java.io.*;
import java.util.Arrays;
import ecs.IECSNode;
import logger.KVOut;

public class ECSClient implements IECSClient {
    
	private static Map<String,String[]> config;
    private static KVOut kv_out = new KVOut("ECS");
    private ZKWatcher zkClient;

	public ECSClient(String configFile){
		try {
			zkClient= new ZKWatcher("localhost:2181",kv_out,importConfig(configFile));
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
    		
        return null;
    }

    @Override
    public boolean awaitNodes(int count, int timeout) throws Exception {
        // TODO
        return false;
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
    
    public static void main(String[] args) {
        kv_out.enableLog("logs/ECS.log", Level.ALL);
        ECSClient admin = new ECSClient(args[0]);
    		ZKWatcher zkClient = admin.zkClient;
    		zkClient.connect();
    		zkClient.setupServer();
    		zkClient.disconnect();
    }
}



