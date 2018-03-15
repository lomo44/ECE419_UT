package testing.CommunicationTests;

import app_kvClient.Commands.KVCommandGet;
import app_kvClient.Commands.KVCommandPut;
import app_kvClient.KVClient;
import app_kvServer.KVServer;
import common.KVMessage;
import common.enums.eKVLogLevel;
import common.messages.KVJSONMessage;
import common.metadata.KVMetadataController;
import common.networknode.KVNetworkNode;
import common.networknode.KVStorageNode;
import junit.framework.TestCase;
import org.junit.Test;
import utility.KVPutGetGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class KVMigrationTest extends TestCase{
    private KVServer serverA;
    private KVServer serverB;
    private KVClient clientA;
    private KVClient clientB;
    private KVPutGetGenerator generatorA = new KVPutGetGenerator(100,0);
    //private KVPutGetGenerator generatorB = new KVPutGetGenerator(100,0);
    private final static int entryCount = 10;
    private KVMetadataController mainController = new KVMetadataController();

    private void initializeData(KVClient client, KVPutGetGenerator generator){
        for(int i = 0; i < entryCount; i++){
            KVJSONMessage ret = client.executeCommand(generator.getNextCommand());
            assertEquals(KVJSONMessage.StatusType.PUT_SUCCESS,ret.getStatus());
        }
    }

    @Override
    protected void setUp() throws Exception {
        serverA = new KVServer(0,100,"FIFO","temp1");
        serverB = new KVServer(0,100,"FIFO","temp2");
        clientA = new KVClient();
        clientB = new KVClient();

        mainController.addStorageNode(serverB.getNode());
        mainController.addStorageNode(serverA.getNode());
        clientA.newConnection("localhost",serverA.getPort());
        clientB.newConnection("localhost",serverB.getPort());

        assertEquals(true,clientA.isConnected());
        assertEquals(true,clientB.isConnected());
        clientA.setLogLevel(eKVLogLevel.OFF,eKVLogLevel.OFF);
        clientB.setLogLevel(eKVLogLevel.OFF,eKVLogLevel.OFF);
        initializeData(clientA,generatorA);
        //initializeData(clientB,generatorB);
        super.setUp();
    }
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        clientA.stop();
        clientB.stop();
        serverA.clearStorage();
        serverB.clearStorage();
        serverA.close();
        serverB.close();
    }

    @Test
    public void testHandleMetadataChange_NoReconnect() throws Exception {
        clientA.getStore().setServerReconnectEnable(false);
        clientB.getStore().setServerReconnectEnable(false);
        // filtered out migrated data and persisted data
        HashMap<String,String> map = generatorA.getDataContent();
        Set<String> migratedSet = new HashSet<>();
        Set<String> persistedSet = new HashSet<>();
        KVNetworkNode serverAnode = serverA.getNode();
        //System.out.println("Filter key");
        for (String key: map.keySet()
             ) {
            KVStorageNode responsibleNode = mainController.getResponsibleStorageNode(mainController.hash(key));
//            System.out.println(String.format("Upper: %s",responsibleNode.getHashRange().getUpperBound()));
//            System.out.println(String.format("Key  : %s",mainController.hash(key)));
//            System.out.println(String.format("Lower: %s",responsibleNode.getHashRange().getLowerBound()));
//            System.out.println(String.format("In range: %b",responsibleNode.getHashRange().inRange(mainController.hash(key))));
//            System.out.println(String.format("Responsible node: %s, Original node: %s",responsibleNode.toString(), serverAnode.toString()));
//            System.out.println("-------------------------------------------------");
            if(!serverAnode.equals(responsibleNode)){
                migratedSet.add(key);
            }
            else{
                persistedSet.add(key);
            }
        }
        // Signal Change in metadata
        serverA.handleChangeInMetadata(mainController.getMetaData());
        serverB.handleChangeInMetadata(mainController.getMetaData());

        KVCommandGet getCommand = new KVCommandGet();
        for(String key : migratedSet){
            //System.out.println(String.format("Checking migrated key %s",key));
            getCommand.setKey(key);
            KVJSONMessage responseFromA = clientA.executeCommand(getCommand);
            assertEquals(KVJSONMessage.StatusType.SERVER_NOT_RESPONSIBLE,responseFromA.getStatus());
            KVJSONMessage responseFromB = clientB.executeCommand(getCommand);
            assertEquals(KVJSONMessage.StatusType.GET_SUCCESS, responseFromB.getStatus());
            assertEquals(map.get(key),responseFromB.getValue());
        }
        for(String key : persistedSet){
            getCommand.setKey(key);
            //System.out.println(String.format("Checking persisted key %s",key));
            KVJSONMessage responseFromA = clientA.executeCommand(getCommand);
            assertEquals(KVJSONMessage.StatusType.GET_SUCCESS,responseFromA.getStatus());
            assertEquals(map.get(key),responseFromA.getValue());

            KVJSONMessage responseFromB = clientB.executeCommand(getCommand);
            assertEquals(KVJSONMessage.StatusType.SERVER_NOT_RESPONSIBLE, responseFromB.getStatus());
        }
    }

    @Test
    public void testHandleChangeInMetadata_Reconnect() throws Exception {
        clientA.getStore().setServerReconnectEnable(true);
        clientB.getStore().setServerReconnectEnable(true);
        // filtered out migrated data and persisted data
        HashMap<String,String> map = generatorA.getDataContent();
        serverA.handleChangeInMetadata(mainController.getMetaData());
        serverB.handleChangeInMetadata(mainController.getMetaData());

        serverA.printResponsibleKeyValuePair();
        System.out.printf("\n");
        serverB.printResponsibleKeyValuePair();

        KVCommandGet getCommand = new KVCommandGet();
        for(String key : map.keySet()){
            getCommand.setKey(key);
            KVJSONMessage responseFromB = clientB.executeCommand(getCommand);
            assertEquals(KVJSONMessage.StatusType.GET_SUCCESS, responseFromB.getStatus());
            assertEquals(map.get(key),responseFromB.getValue());
            KVJSONMessage responseFromA = clientA.executeCommand(getCommand);
            assertEquals(KVJSONMessage.StatusType.GET_SUCCESS, responseFromA.getStatus());
            assertEquals(map.get(key),responseFromA.getValue());
        }
    }
}
