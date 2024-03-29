package app_kvServer.daemons;

import app_kvServer.KVServer;
import common.communication.KVCommunicationModuleSet;
import common.enums.eKVExtendStatusType;
import common.enums.eKVNetworkNodeType;
import common.messages.KVJSONMessage;
import common.networknode.KVStorageCluster;
import common.networknode.KVStorageNode;

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class KVClusterUpdateDaemon implements Runnable{
    KVServer serverInstance;
    BlockingQueue<KVJSONMessage> updateQueue = new LinkedBlockingDeque<>();
    KVCommunicationModuleSet communicationModuleSet;
    Thread updateThread = new Thread(this);
    boolean running = false;
    boolean checkAckEnable = false;

    public KVClusterUpdateDaemon(KVServer serverInstance, boolean checkAckEnable){
        this.serverInstance = serverInstance;
        this.checkAckEnable = checkAckEnable;
        this.communicationModuleSet = new KVCommunicationModuleSet();
        updateThread.setName(String.format("%s-updateDaemon",serverInstance.getUID()));
    }
    public KVClusterUpdateDaemon(KVServer serverInstance){
        this(serverInstance,false);
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
    public void stop() throws InterruptedException {
        this.running = false;
        this.updateThread.interrupt();
        this.updateThread.join();
        this.communicationModuleSet.close();
    }

    public void start(){
        this.updateThread.start();
    }

    public void queueMessage(KVJSONMessage msg) throws InterruptedException {
        this.updateQueue.put(msg);
    }

    public boolean isRunning(){
        return running;
    }
    private boolean forwardUpdate(KVJSONMessage msg){
        boolean ret = true;
        KVStorageNode node = serverInstance.getResponsibleNode(msg.getKey());
        if(node.getNodeType()== eKVNetworkNodeType.STORAGE_CLUSTER){
            KVStorageCluster cluster = (KVStorageCluster)node;
            Collection<KVStorageNode> children = cluster.getChildNodes();
            if(children!=null){
                if(checkAckEnable){
                    Vector<KVJSONMessage> acks = communicationModuleSet.syncBroadcast(msg,children);
                    if(acks.size()==children.size()){
                        for (KVJSONMessage ack: acks
                                ) {
                            if(ack.getExtendStatusType()!=eKVExtendStatusType.REPLICA_OK){
                                ret = false;
                                break;
                            }
                        }
                    }
                    else {
                        ret = false;
                    }
                }
                else{
                    communicationModuleSet.asyncBroadcastSend(msg,children);
                }
            }
        }
        return ret;
    }
}
