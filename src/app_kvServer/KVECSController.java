package app_kvServer;

import common.messages.KVJSONMessage;
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
    private CountDownLatch connectionLatch = new CountDownLatch(1);
    private boolean connected = false;
    private Watcher metadataWacher;
    private Watcher configDataWatcher;
    private Watcher mainWatcher;

    public KVECSController(KVServer server, KVMetadataController metadataController){
        this.server = server;
        this.metadataController = metadataController;
    }

    public void connect(String uniqueName, String zk_hostname, int zk_port) throws IOException, InterruptedException, KeeperException {
        this.uniqueName = uniqueName;
        initConfigDataWatcher();
        initMetadataWatcher();
        initMainWatcher();
        zk = new ZooKeeper(createConnectionString(uniqueName, zk_hostname, zk_port), 5000,mainWatcher);
        connectionLatch.wait();
        if(!connected){
            throw new IOException("Unable to create connection");
        }
        processConfigData();
        connectionLatch.wait();
        processMetaData();
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
        mainWatcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                switch (event.getState()){
                    case ConnectedReadOnly:
                    case SaslAuthenticated:
                    case NoSyncConnected:
                    case SyncConnected:{
                        connected = true;
                        connectionLatch.countDown();
                        break;
                    }
                    default:{
                        connectionLatch.countDown();
                        break;
                    }
                }
            }
        };
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
            KVMetadata newMetaddata = KVMetadata.fromBytes(data);
            server.handleChangeInMetadata(newMetaddata);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processConfigData(){
        try{
            byte[] data = zk.getData(getCurrentConfigPath(),configDataWatcher,null);
            KVJSONMessage msg = new KVJSONMessage();
            msg.fromBytes(data,0,data.length);
            KVServerConfig config = KVServerConfig.fromKVJSONMessage(msg);
            server.handleChangeInConfigData(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
