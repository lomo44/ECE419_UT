package testing.IntegrationTests;

import app_kvClient.Commands.KVCommandGet;
import app_kvClient.Commands.KVCommandPut;
import app_kvClient.KVClient;
import app_kvServer.KVServer;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;
import common.messages.KVMessage;
import junit.framework.TestCase;
import org.junit.Test;
import testing.KVTestPortManager;

public class PersistencyFIFOServerTest extends TestCase {

    protected String cacheStratagies = "FIFO";
    protected KVClient client = null;
    protected KVServer server = null;
    protected int port;

    @Override
    public void setUp() throws Exception{
        port = KVTestPortManager.port.incrementAndGet();
        client = new KVClient(System.in);
    }

    @Override
    public void tearDown() throws Exception{
        if(client!=null){
            client.disconnect();
            client = null;
        }
        if(server!=null){
            server.close();
            server = null;
        }

    }

    @Test
    public void testServerDropOut() throws Exception {
        // Spooling up the server
        server = new KVServer(port,10,cacheStratagies);
        // Connect to the server
        client.newConnection("localhost",port);
        assertTrue(client.isConnected());
        assertTrue(server.isHandlerRunning());
        // Issue a put to the server
        KVCommandPut cmdInstance = new KVCommandPut();
        cmdInstance.setKey("Hello");
        cmdInstance.setValue("World");
        KVJSONMessage response = client.executeCommand(cmdInstance);
        assertTrue(response.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        // Close the server
        server.close();
        server = null;
        server = new KVServer(port,10,cacheStratagies);
        KVCommandGet cmdGet = new KVCommandGet();
        cmdGet.setKey("Hello");
        response = client.executeCommand(cmdGet);

        assertEquals(eKVExtendStatusType.NO_RESPONSE,response.getExtendStatusType());
        assertTrue(!client.isConnected());

        client.newConnection("localhost",port);
        response = client.executeCommand(cmdGet);
        assertEquals(KVMessage.StatusType.GET_SUCCESS,response.getStatus());
        assertEquals("World",response.getValue());
    }
}
