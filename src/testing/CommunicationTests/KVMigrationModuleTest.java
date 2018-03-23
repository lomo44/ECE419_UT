package testing.CommunicationTests;

import app_kvClient.Commands.KVCommandGet;
import app_kvClient.KVClient;
import app_kvServer.KVMigrationModule;
import app_kvServer.KVServer;
import common.KVMessage;
import common.communication.KVCommunicationModuleSet;
import common.messages.KVJSONMessage;
import common.messages.KVMigrationMessage;
import junit.framework.TestCase;
import org.junit.Test;
import utility.KVPutGetGenerator;

import java.io.IOException;
import java.util.Vector;

import static common.enums.eKVExtendStatusType.MIGRATION_COMPLETE;

public class KVMigrationModuleTest extends TestCase {
    private Vector<KVServer> serverList = new Vector<>();
    private Vector<KVClient> clientList = new Vector<>();
    private KVPutGetGenerator generator;
    private KVMigrationModule migrationModule = new KVMigrationModule();

    public int numOfClientServerPair = 10;
    public int msgPerServer = 7;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        for(int i =0;i < numOfClientServerPair; i++){
            KVServer newServer = new KVServer(0,100,"LRU");
            newServer.run();
            int port = newServer.getPort();
            KVClient newClient = new KVClient();
            newClient.newConnection("localhost",port);
            assertEquals(true,newClient.isConnected());
            serverList.add(newServer);
            clientList.add(newClient);
        }

    }

    @Override
    protected void tearDown() throws Exception {
        for (KVServer server: serverList
             ) {
            server.clearStorage();
            server.close();
        }
        for (KVClient client: clientList
                ) {
            client.stop();
        }
        super.tearDown();
    }

    @Test
    public void testKVMigrationModule_Basic() throws IOException {
        String tag = "AddressInfo";
        for (KVServer server: serverList){
            KVMigrationMessage msg = new KVMigrationMessage();
            msg.put(tag,server.getStorageNode().toString());
            KVJSONMessage ret = migrationModule.migrate(server.getStorageNode(),msg);
            assertEquals(MIGRATION_COMPLETE,ret.getExtendStatusType());
        }
        KVCommandGet getCommand = new KVCommandGet();
        getCommand.setKey(tag);
        for(int i = 0; i < clientList.size();i++){
            KVMessage getResponse = clientList.elementAt(i).executeCommand(getCommand);
            assertEquals(serverList.elementAt(i).getStorageNode().toString(),getResponse.getValue());
        }
    }
    @Test
    public void testKVMigrationModule_MultiThreaded(){
        final String tag = "AddressInfo";
        for(final KVServer server :serverList){
            Thread newThread = new Thread(){
                @Override
                public void run() {
                    KVMigrationMessage msg = new KVMigrationMessage();
                    msg.put(tag,server.getStorageNode().toString());
                    KVJSONMessage ret = null;
                    try {
                        ret = migrationModule.migrate(server.getStorageNode(),msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assertEquals(MIGRATION_COMPLETE,ret.getExtendStatusType());
                }
            };
            newThread.start();
        }
        KVCommandGet getCommand = new KVCommandGet();
        getCommand.setKey(tag);
        for(int i = 0; i < clientList.size();i++){
            KVMessage getResponse = clientList.elementAt(i).executeCommand(getCommand);
            assertEquals(serverList.elementAt(i).getStorageNode().toString(),getResponse.getValue());
        }
    }
}
