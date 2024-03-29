package app_kvServer;


import common.communication.KVCommunicationModule;
import common.communication.KVCommunicationModuleSet;
import common.enums.eKVExtendStatusType;
import common.enums.eKVNetworkNodeType;
import common.messages.KVJSONMessage;
import common.messages.KVMigrationMessage;
import common.networknode.KVNetworkNode;
import common.networknode.KVStorageCluster;
import common.networknode.KVStorageNode;

import java.io.IOException;
import java.net.SocketException;

public class KVMigrationModule {

    private KVCommunicationModuleSet connectionTable;

    public KVMigrationModule(){
        this.connectionTable = new KVCommunicationModuleSet();
    }

    /**
     * Close the migration module
     */
    public void close(){
        //Iterate through the cocmmunication module and stop the connection
       connectionTable.close();
    }

    /**
     * Migrate the content of the KVMigrationMessage to the specific storage node server
     * @param outputNode target storage node
     * @param msg migration message
     * @return server response
     * @throws IOException thrown if the connection between the servers cannot be made
     */
    public KVJSONMessage clusterExternalMigration(KVNetworkNode outputNode, KVMigrationMessage msg) throws IOException {
        switch (outputNode.getNodeType()){
            case NETWORK_NODE:
            case STORAGE_NODE:
            case STORAGE_CLUSTER:{
                return clusterExternalMigration((KVStorageNode)outputNode,msg);
            }
        }
        return null;
    }

    private KVJSONMessage clusterExternalMigration(KVStorageNode outputNode, KVMigrationMessage msg) throws IllegalArgumentException, IOException {
        if(outputNode.getNodeType() == eKVNetworkNodeType.STORAGE_CLUSTER){
            KVStorageNode primary = ((KVStorageCluster)(outputNode)).getPrimaryNode();
            if(primary==null){
                KVStorageCluster cluster = (KVStorageCluster) outputNode;
                return clusterExternalMigration(cluster.getRandomMember(),msg);
            }
            else{
                return clusterExternalMigration(primary,msg);
            }
        }
        else{
            if(!connectionTable.containsKey(outputNode)){
                connectionTable.add(outputNode);
            }
            // Find the communication module initiates the migration
            KVCommunicationModule module = connectionTable.getCommunicationModule(outputNode);
            KVJSONMessage outputmsg = msg.toKVJSONMessage();
            outputmsg.setExtendStatus(eKVExtendStatusType.MIGRATION_DATA);
            module.send(outputmsg);
            return module.receive();
        }
    }
    public void clusterInternalMigration(KVStorageCluster targetCluster, KVMigrationMessage msg){
        KVJSONMessage outputmsg = msg.toKVJSONMessage();
        outputmsg.setExtendStatus(eKVExtendStatusType.PRIMARY_MIGRATE);
        msg.setIsRequiredAck(false);
        connectionTable.asyncBroadcastSend(outputmsg,targetCluster.getChildNodesWithoutPrimary());
    }
    public KVJSONMessage syncPrimaryForwardMigration(KVStorageNode targetNode, KVMigrationMessage msg){
        KVJSONMessage outputmsg = msg.toKVJSONMessage();
        outputmsg.setExtendStatus(eKVExtendStatusType.PRIMARY_FORWARD_MIGRATE);
        return connectionTable.syncSend(outputmsg,targetNode);
    }

    public boolean syncReplicaForwardMigration(KVStorageCluster targetCluster, KVMigrationMessage msg){
        //TODO: added timeout for failure protection.
        KVJSONMessage outputmsg = msg.toKVJSONMessage();
        outputmsg.setExtendStatus(eKVExtendStatusType.REPLICA_FORWARD_MIGRATE);
        KVJSONMessage ret = connectionTable.syncSend(outputmsg,targetCluster.getPrimaryNode());
        if(ret!=null && ret.getExtendStatusType()==eKVExtendStatusType.PRIMARY_OK){
            return true;
        }
        return false;
    }
}
