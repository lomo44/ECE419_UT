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
        for (KVNetworkNode node: connectionTable.keySet()
             ) {
            KVCommunicationModule module = connectionTable.get(node);
            try {
                module.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Migrate the content of the KVMigrationMessage to the specific storage node server
     * @param outputNode target storage node
     * @param msg migration message
     * @return server response
     * @throws IOException thrown if the connection between the servers cannot be made
     */
    public KVJSONMessage migrate(KVNetworkNode outputNode, KVMigrationMessage msg) throws IOException {
        switch (outputNode.getNodeType()){
            case NETWORK_NODE:
            case STORAGE_NODE:
            case STORAGE_CLUSTER:{
                return migrate((KVStorageNode)outputNode,msg);
            }
        }
        return null;
    }

    private KVJSONMessage migrate(KVStorageNode outputNode, KVMigrationMessage msg) throws SocketException {
        if(outputNode.getNodeType() == eKVNetworkNodeType.STORAGE_CLUSTER){
            KVStorageNode primary = ((KVStorageCluster)(outputNode)).getPrimaryNode();
            return migrate(primary,msg);
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

}
