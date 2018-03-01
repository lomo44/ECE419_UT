package testing.CommunicationTests;

import app_kvClient.KVClient;
import app_kvServer.KVServer;
import junit.framework.TestCase;
import org.junit.Test;
import utility.KVPutGetGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class KVMigrationModuleTest extends TestCase {
    public Vector<KVServer> serverList = new Vector<>();
    public Vector<KVClient> clientList = new Vector<>();
    public KVPutGetGenerator generator;

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
            server.close();
        }
        for (KVClient client: clientList
                ) {
            client.stop();
        }
        super.tearDown();
    }

    @Test
    public void testKVMigrationModule_Basic(){


    }
}
