package app_kvServer.daemons;

import app_kvServer.KVServer;
import common.communication.KVCommunicationModule;
import common.communication.KVCommunicationModuleSet;
import common.enums.eKVExtendStatusType;
import common.enums.eKVNetworkNodeType;
import common.messages.KVJSONMessage;
import common.networknode.KVStorageCluster;
import common.networknode.KVStorageNode;

import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class KVClusterUpdateDaemon implements Runnable{
    KVServer serverInstance;
    BlockingQueue<KVJSONMessage> updateQueue = new LinkedBlockingDeque<>();
    KVCommunicationModuleSet communicationModuleSet  = new KVCommunicationModuleSet();
    boolean running = false;
    boolean checkAckEnable = false;

    public KVClusterUpdateDaemon(KVServer serverInstance, boolean checkAckEnable){
        this.serverInstance = serverInstance;
        this.checkAckEnable = checkAckEnable;
    }
    public KVClusterUpdateDaemon(KVServer serverInstance){
        this.serverInstance = serverInstance;
    }

    @Override
    public void run() {
        running = true;
        System.out.println("Cluster Update Daemon starts...");
        while(running || updateQueue.size()!=0){
            try {
                forwardUpdate(updateQueue.take());
            } catch (InterruptedException e) {
                continue;
            }
        }
        System.out.println("Cluster Update Daemon exits..");
    }
    public void close(){
        this.running = false;
    }

    public void queueMessage(KVJSONMessage msg) throws InterruptedException {
        this.updateQueue.put(msg);
    }

    private boolean forwardUpdate(KVJSONMessage msg){
        boolean ret = true;
        KVStorageNode node = serverInstance.getResponsibleNode(msg.getKey());
        if(node.getNodeType()== eKVNetworkNodeType.STORAGE_CLUSTER){
            KVStorageCluster cluster = (KVStorageCluster)node;
            Collection<KVStorageNode> children = cluster.getChildNodes();
            for(KVStorageNode child : children){
                KVCommunicationModule module = communicationModuleSet.getCommunicationModule(child);
                try {
                    msg.setExtendStatus(eKVExtendStatusType.PRIMARY_UPDATE);
                    module.send(msg);
                    KVJSONMessage response = module.receive();
                    if(checkAckEnable && response.getExtendStatusType() != eKVExtendStatusType.REPLICA_OK){
                        ret = false;
                    }
                } catch (SocketException e) {

                }
            }
        }
        return ret;
    }
}
