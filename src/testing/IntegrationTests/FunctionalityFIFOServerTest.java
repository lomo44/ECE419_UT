package testing.IntegrationTests;


import app_kvClient.Commands.KVCommandEcho;
import app_kvClient.Commands.KVCommandGet;
import app_kvClient.Commands.KVCommandPut;
import app_kvClient.KVClient;
import app_kvServer.KVServer;
import common.KVMessage;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;
import junit.framework.TestCase;
import org.junit.Test;
import testing.KVTestPortManager;

public class FunctionalityFIFOServerTest extends TestCase {


    protected KVServer server = null;
    protected KVClient client = null;
    protected int port;


    @Override
    protected void setUp() throws Exception{
        port = KVTestPortManager.port.incrementAndGet();
        server = new KVServer(port, 10, "FIFO");
        client = new KVClient(System.in);
        client.newConnection("localhost",port);
    }

    @Override
    protected void tearDown() throws Exception{
        client.disconnect();
        server.clearStorage();
        server.close();
        //Thread.sleep(1000);
        server = null;
        client = null;
    }

    @Test
    public void testClientBasic_Connection(){
        assertTrue(client.isConnected());
    }
    @Test
    public void testClientBasic_PutSuccess(){
        assertTrue(client.isConnected());
        assertTrue(server.isHandlerRunning());
        KVCommandPut cmdInstance = new KVCommandPut();
        cmdInstance.setKey("Hello");
        cmdInstance.setValue("World");
        KVMessage response = client.executeCommand(cmdInstance);
        assertTrue(response.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
    }
    @Test
    public void testClientBasic_BadKey(){
        assertTrue(client.isConnected());
        assertTrue(server.isHandlerRunning());
        KVCommandPut cmdInstance = new KVCommandPut();
        cmdInstance.setKey("");
        cmdInstance.setValue("World");
        KVMessage response = client.executeCommand(cmdInstance);
        assertEquals(KVMessage.StatusType.PUT_ERROR,response.getStatus());
    }

    @Test
    public void testClientBasic_GetError(){
        assertTrue(server.isHandlerRunning());
        assertTrue(client.isConnected());
        KVCommandGet cmdInstance = new KVCommandGet();
        cmdInstance.setKey("Hello");
        KVMessage response = client.executeCommand(cmdInstance);
        assertTrue(response.getStatus() == KVMessage.StatusType.GET_ERROR);
    }
    @Test
    public void testClientBasic_GetSuccess(){
        assertTrue(server.isHandlerRunning());
        assertTrue(client.isConnected());
        KVCommandPut putInstance = new KVCommandPut();
        putInstance.setKey("Hello");
        putInstance.setValue("World");
        KVMessage putResponse = client.executeCommand(putInstance);
        assertTrue(putResponse.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        KVCommandGet getInstance = new KVCommandGet();
        getInstance.setKey("Hello");
        KVMessage getResponse = client.executeCommand(getInstance);
        assertTrue(getResponse.getStatus() == KVMessage.StatusType.GET_SUCCESS);
        assertTrue(getResponse.getValue().matches(putInstance.getValue()));
    }
    @Test
    public void testClientBasic_DeleteSuccess(){
        assertTrue(server.isHandlerRunning());
        assertTrue(client.isConnected());
        KVCommandPut putInstance = new KVCommandPut();
        putInstance.setKey("Hello");
        putInstance.setValue("World 123");
        KVMessage putResponse = client.executeCommand(putInstance);
        assertTrue(putResponse.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        putInstance.setValue("null");
        KVMessage deleteResponse = client.executeCommand(putInstance);
        assertEquals(deleteResponse.getStatus(),KVMessage.StatusType.DELETE_SUCCESS);
    }
    @Test
    public void testClientBasic_DeleteSuccess2(){
        assertTrue(server.isHandlerRunning());
        assertTrue(client.isConnected());
        KVCommandPut putInstance = new KVCommandPut();
        putInstance.setKey("Hello");
        putInstance.setValue("World 123");
        KVMessage putResponse = client.executeCommand(putInstance);
        assertTrue(putResponse.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        putInstance.setValue("");
        KVMessage deleteResponse = client.executeCommand(putInstance);
        assertEquals(deleteResponse.getStatus(),KVMessage.StatusType.DELETE_SUCCESS);
    }

    @Test
    public void testClientBasic_DeleteErrorEmptyKey(){
        assertTrue(server.isHandlerRunning());
        assertTrue(client.isConnected());
        KVCommandPut putInstance = new KVCommandPut();
        putInstance.setKey("Hello");
        putInstance.setValue("World 123");
        KVMessage putResponse = client.executeCommand(putInstance);
        assertTrue(putResponse.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        putInstance.setKey("");
        putInstance.setValue("");
        KVMessage deleteResponse = client.executeCommand(putInstance);
        assertEquals(deleteResponse.getStatus(),KVMessage.StatusType.DELETE_ERROR);
    }

    @Test
    public void testClientBasic_DeleteError(){
        assertTrue(server.isHandlerRunning());
        assertTrue(client.isConnected());
        KVCommandPut putInstance = new KVCommandPut();
        putInstance.setKey("Hello");
        putInstance.setValue("null");
        KVMessage putResponse = client.executeCommand(putInstance);
        assertEquals(putResponse.getStatus(),KVMessage.StatusType.DELETE_ERROR);
    }
    @Test
    public void testClientBasic_UpdateSuccess(){
        assertTrue(server.isHandlerRunning());
        assertTrue(client.isConnected());
        KVCommandPut putInstance = new KVCommandPut();
        putInstance.setKey("Hello");
        putInstance.setValue("World");
        KVMessage putResponse = client.executeCommand(putInstance);
        assertTrue(putResponse.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        putInstance.setValue("Underworld");
        KVMessage deleteResponse = client.executeCommand(putInstance);
        assertTrue(deleteResponse.getStatus() == KVMessage.StatusType.PUT_UPDATE);
    }
    @Test
    public void testClientBasic_Echo() {
        assertTrue(server.isHandlerRunning());
        assertTrue(client.isConnected());
        KVCommandEcho echoInstance = new KVCommandEcho();
        KVJSONMessage echoResponse = client.executeCommand(echoInstance);
        assertEquals(eKVExtendStatusType.ECHO,echoResponse.getExtendStatusType());
    }
}

