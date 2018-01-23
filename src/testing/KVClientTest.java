package testing;


import app_kvClient.KVClient;
import app_kvClient.Commands.*;
import app_kvServer.KVServer;
import app_kvServer.echoServer.KVServerEcho;
import common.messages.KVMessage;
import org.junit.Test;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class KVClientTest extends TestCase {
    @Test
    public void testClientBasic_Connection() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(40001,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(serverEcho.isHandlerRunning());
        KVClient client = new KVClient();
        try {
            client.newConnection("localhost",40001);
        } catch (Exception e) {
            System.out.println("Error! Could not connect to server!");
        }
        TimeUnit.SECONDS.sleep(1);
        assertTrue(client.isConnected());
        client.disconnect();
        serverEcho.close();
    }
    @Test
    public void testClientBasic_PutSuccess() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(40002,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(serverEcho.isHandlerRunning());
        KVClient client = new KVClient();
        try {
            client.newConnection("localhost",40002);
        } catch (Exception e) {
            System.out.println("Error! Could not connect to server!");
        }
        TimeUnit.SECONDS.sleep(1);
        assertTrue(client.isConnected());
        KVCommandPut cmdInstance = new KVCommandPut();
        cmdInstance.setKey("Hello");
        cmdInstance.setValue("World");
        KVMessage response = client.executeCommand(cmdInstance);
        assertTrue(response.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        client.disconnect();
        serverEcho.close();
    }
    @Test
    public void testClientBasic_GetError() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(40003,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(serverEcho.isHandlerRunning());
        KVClient client = new KVClient();
        try {
            client.newConnection("localhost",40003);
        } catch (Exception e) {
            System.out.println("Error! Could not connect to server!");
        }
        TimeUnit.SECONDS.sleep(1);
        assertTrue(client.isConnected());
        KVCommandGet cmdInstance = new KVCommandGet();
        cmdInstance.setKey("Hello");
        KVMessage response = client.executeCommand(cmdInstance);
        assertTrue(response.getStatus() == KVMessage.StatusType.GET_ERROR);
        client.disconnect();
        serverEcho.close();
    }
    @Test
    public void testClientBasic_GetSuccess() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(40004,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(serverEcho.isHandlerRunning());
        KVClient client = new KVClient();
        try {
            client.newConnection("localhost",40004);
        } catch (Exception e) {
            System.out.println("Error! Could not connect to server!");
        }
        TimeUnit.SECONDS.sleep(1);
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
        assertTrue(getResponse.getValue() == putInstance.getValue());
        client.disconnect();
        serverEcho.close();
    }
    @Test
    public void testClientBasic_DeleteSuccess() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(40005,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(serverEcho.isHandlerRunning());
        KVClient client = new KVClient();
        try {
            client.newConnection("localhost",40005);
        } catch (Exception e) {
            System.out.println("Error! Could not connect to server!");
        }
        TimeUnit.SECONDS.sleep(1);
        assertTrue(client.isConnected());
        KVCommandPut putInstance = new KVCommandPut();
        putInstance.setKey("Hello");
        putInstance.setValue("World");
        KVMessage putResponse = client.executeCommand(putInstance);
        assertTrue(putResponse.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        putInstance.setValue(null);
        KVMessage deleteResponse = client.executeCommand(putInstance);
        assertTrue(deleteResponse.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        client.disconnect();
        serverEcho.close();
    }
    @Test
    public void testClientBasic_DeleteError() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(40006,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(serverEcho.isHandlerRunning());
        KVClient client = new KVClient();
        try {
            client.newConnection("localhost",40006);
        } catch (Exception e) {
            System.out.println("Error! Could not connect to server!");
        }
        TimeUnit.SECONDS.sleep(1);
        assertTrue(client.isConnected());
        KVCommandPut putInstance = new KVCommandPut();
        putInstance.setKey("Hello");
        putInstance.setValue(null);
        KVMessage putResponse = client.executeCommand(putInstance);
        assertTrue(putResponse.getStatus() == KVMessage.StatusType.DELETE_ERROR);
        client.disconnect();
        serverEcho.close();
    }
    @Test
    public void testClientBasic_UpdateSuccess() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(40007,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(serverEcho.isHandlerRunning());
        KVClient client = new KVClient();
        try {
            client.newConnection("localhost",40007);
        } catch (Exception e) {
            System.out.println("Error! Could not connect to server!");
        }
        TimeUnit.SECONDS.sleep(1);
        assertTrue(client.isConnected());
        KVCommandPut putInstance = new KVCommandPut();
        putInstance.setKey("Hello");
        putInstance.setValue("World");
        KVMessage putResponse = client.executeCommand(putInstance);
        assertTrue(putResponse.getStatus() == KVMessage.StatusType.PUT_SUCCESS);
        putInstance.setValue("Underworld");
        KVMessage deleteResponse = client.executeCommand(putInstance);
        assertTrue(deleteResponse.getStatus() == KVMessage.StatusType.PUT_UPDATE);
        client.disconnect();
        serverEcho.close();
    }
    @Test
    public void testClientBasic_Echo() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(40007,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(serverEcho.isHandlerRunning());
        KVClient client = new KVClient();
        try {
            client.newConnection("localhost",40007);
        } catch (Exception e) {
            System.out.println("Error! Could not connect to server!");
        }
        TimeUnit.SECONDS.sleep(1);
        assertTrue(client.isConnected());
        KVCommandEcho echoInstance = new KVCommandEcho();
        KVMessage echoResponse = client.executeCommand(echoInstance);
        assertTrue(echoResponse.getStatus() == KVMessage.StatusType.ECHO);
        client.disconnect();
        serverEcho.close();
    }
}

