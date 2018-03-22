package app_kvServer;

import app_kvServer.daemons.KVClusterUpdateDaemon;
import common.communication.KVCommunicationModule;
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
        this.communicationModuleSet = communicationModuleSet;
        this.clusterUpdateDaemon = new KVClusterUpdateDaemon(serverInstance);
        this.serverInstance = serverInstance;
    }
    public void close(){
        try {
            this.clusterUpdateDaemon.close();
        } catch (InterruptedException e) {

        }
    }
    public void queueClusterUpdate(KVJSONMessage msg){
        try {
            this.clusterUpdateDaemon.queueMessage(msg);
        } catch (InterruptedException e) {

        }
    }
    public void announcePrimary(String primaryUID, String clusterUID){
        KVStorageNode node = serverInstance.getResponsibleNode(clusterUID);
        if(node.getNodeType()== eKVNetworkNodeType.STORAGE_CLUSTER){
            KVStorageCluster cluster = (KVStorageCluster)node;
            KVPrimaryDeclarationMessage msg = new KVPrimaryDeclarationMessage();
            msg.setPrimaryID(primaryUID);
            msg.setClusterID(clusterUID);
            communicationModuleSet.asyncBroadcastSend(msg.toKVJSONMessage(),cluster.getChildNodes());
        }
    }
}
