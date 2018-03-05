package app_kvServer;

import common.metadata.KVMetadata;
import common.metadata.KVMetadataController;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class KVECSController {
    ZooKeeper zk;

    private final static String SERVER_ROOT_PATH = "/server";
    private final static String SERVER_CONFIG_NAME = "config";
    private final static String SERVER_META_NAME = "meta";
    private String uniqueName;
    private KVServer server;
    private KVMetadataController metadataController;
    private CountDownLatch latch;
    private boolean connected = false;
    private Watcher metadataWacher;
    private Watcher configDataWatcher;
    private Watcher mainWatcher;

    public KVECSController(KVServer server, KVMetadataController metadataController){
        this.server = server;
        this.metadataController = metadataController;
    }

    public void connect(String uniqueName, String zk_hostname, int zk_port) throws IOException, InterruptedException, KeeperException {
        zk = new ZooKeeper(createConnectionString(uniqueName, zk_hostname, zk_port), 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                switch (event.getState()){
                    case ConnectedReadOnly:
                    case SaslAuthenticated:
                    case NoSyncConnected:
                    case SyncConnected:{
                        connected = true;
                        latch.countDown();
                        break;
                    }
                    default:{
                        latch.countDown();
                        break;
                    }
                }
            }
        });
        latch.await();
        if(!connected){
            throw new IOException("Unable to create connection");
        }
        initConfigDataWatcher();
        initMetadataWatcher();
    }

    private String createConnectionString(String uniqueName, String zk_hostname, int zk_port){
        return zk_hostname+":"+Integer.toString(zk_port)+SERVER_ROOT_PATH+"/"+uniqueName+"/";
    }

    private void initMetadataWatcher() throws KeeperException, InterruptedException {
        metadataWacher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                switch (event.getType()){
                    case NodeChildrenChanged:
                    case NodeDataChanged:
                    case NodeCreated:{
                        processMetaData();
                        break;
                    }
                }
            }
        };
        processMetaData();
    }

    private void initConfigDataWatcher() throws KeeperException, InterruptedException {
        configDataWatcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                switch (event.getType()){
                    case NodeChildrenChanged:
                    case NodeDataChanged:
                    case NodeCreated:{
                        processMetaData();
                        break;
                    }
                }
            }
        };
        processMetaData();
    }

    private void initMainWatcher(){
        
    }

    private String getCurrentServerRootPath(){
        return SERVER_ROOT_PATH+"/"+uniqueName;
    }

    private String getCurrentMetadataPath(){
        return getCurrentServerRootPath()+"/"+SERVER_META_NAME;
    }

    private String getCurrentConfigPath(){
        return getCurrentConfigPath()+"/"+SERVER_CONFIG_NAME;
    }

    private void processMetaData(){
        try {
            byte[] data = zk.getData(getCurrentMetadataPath(),metadataWacher,null);
            metadataController.update(KVMetadata.fromBytes(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processConfigData(){
        //TODO: finish
    }

}
