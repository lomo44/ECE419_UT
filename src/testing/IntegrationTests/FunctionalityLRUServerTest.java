package testing.IntegrationTests;


import app_kvClient.KVClient;
import app_kvClient.Commands.*;
import app_kvServer.KVServer;
import app_kvServer.echoServer.KVServerEcho;
import common.messages.KVMessage;
import org.junit.Test;

import junit.framework.TestCase;
import testing.KVTestPortManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FunctionalityLRUServerTest extends TestCase {

    private KVServer server = null;
    private KVClient client = null;
    private int port;

    @Override
    protected void setUp() throws Exception{
        port = KVTestPortManager.port.incrementAndGet();
        server = new KVServer(port,10, "LRU");
        client = new KVClient();
        client.newConnection("localhost",port);
    }

    @Override
    protected void tearDown() throws Exception{
        client.disconnect();
        server.clearStorage();
        server.close();
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
        putInstance.setValue("World");
        KVMessage putResponse = client.executeCommand(putInstance);
        assertTrue(putResponse.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        putInstance.setValue("null");
        KVMessage deleteResponse = client.executeCommand(putInstance);
        assertEquals(deleteResponse.getStatus(),KVMessage.StatusType.DELETE_SUCCESS);
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
        KVMessage echoResponse = client.executeCommand(echoInstance);
        assertTrue(echoResponse.getStatus() == KVMessage.StatusType.ECHO);
    }
}

