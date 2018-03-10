package app_kvServer;


import common.communication.KVCommunicationModule;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;
import common.messages.KVMigrationMessage;
import common.networknode.KVNetworkNode;
import common.networknode.KVStorageNode;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class KVMigrationModule {

    private HashMap<KVNetworkNode,KVCommunicationModule> connectionTable;

    public KVMigrationModule(){
        this.connectionTable = new HashMap<>();
    }

    /**
     * Close the migration module
     */
    public void close(){
        //Iterate through the cocmmunication module and close the connection
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
        if(!connectionTable.containsKey(outputNode)){
            // Previous Connection doesn't exist, need to create a new one
            Socket newConnection = outputNode.createSocket();
            KVCommunicationModule newCommunicationModule = new KVCommunicationModule(newConnection,"localhost");
            connectionTable.put(outputNode,newCommunicationModule);
        }
        // Find the communication module initiates the migration
        KVCommunicationModule module = connectionTable.get(outputNode);
        KVJSONMessage outputmsg = msg.toKVJSONMessage();
        outputmsg.setExtendStatus(eKVExtendStatusType.MIGRATION_DATA);
        module.send(outputmsg);
        return module.receiveMessage();
    }
}
