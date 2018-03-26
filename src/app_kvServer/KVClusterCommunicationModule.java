package app_kvServer;

import app_kvServer.daemons.KVClusterUpdateDaemon;
import common.communication.KVCommunicationModuleSet;
import common.enums.eKVNetworkNodeType;
import common.messages.KVJSONMessage;
import common.messages.KVPrimaryDeclarationMessage;
import common.networknode.KVStorageCluster;
import common.networknode.KVStorageNode;

public class KVClusterCommunicationModule {
    KVCommunicationModuleSet communicationModuleSet;
    KVClusterUpdateDaemon clusterUpdateDaemon;
    KVServer serverInstance;
    public KVClusterCommunicationModule(KVServer serverInstance){
        this.communicationModuleSet = new KVCommunicationModuleSet();
        this.clusterUpdateDaemon = new KVClusterUpdateDaemon(serverInstance);
        this.serverInstance = serverInstance;
    }
    public void close(){
        if(this.clusterUpdateDaemon.isRunning()){
            stopUpdateDaemon();
        }
        this.communicationModuleSet.close();
    }
    public void startUpdateDaemon(){
        if(!this.clusterUpdateDaemon.isRunning()){
            this.clusterUpdateDaemon.start();
        }
    }
    public void stopUpdateDaemon(){
        try {
            this.clusterUpdateDaemon.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void queueClusterUpdate(KVJSONMessage msg){
        try {
            this.clusterUpdateDaemon.queueMessage(msg);
        } catch (InterruptedException e) {

        }
    }
    public void announcePrimary(String primaryUID, String clusterUID){
        KVStorageNode node = serverInstance.getMetadataController().getStorageNode(clusterUID);
        if(node.getNodeType()== eKVNetworkNodeType.STORAGE_CLUSTER){
            KVStorageCluster cluster = (KVStorageCluster)node;
            KVPrimaryDeclarationMessage msg = new KVPrimaryDeclarationMessage();
            msg.setPrimaryID(primaryUID);
            msg.setClusterID(clusterUID);
            communicationModuleSet.asyncBroadcastSend(msg.toKVJSONMessage(),cluster.getChildNodes());
        }
    }
}
