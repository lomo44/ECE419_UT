package testing.IntegrationTests;

import app_kvClient.KVClient;
import app_kvServer.KVServer;
import common.messages.KVMessage;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class AutoTester extends TestCase{

    @Test
    public void testM1Autotester() throws Exception {
        KVServer server = new KVServer(0, 5, "LRU");
        server.run();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        KVClient client = new KVClient();
        client.newConnection("localhost", server.getPort());
        client.getStore();
        client.getStore().disconnect();
        server.close();
    }
    @Test
    public void testM1Autotester_2() throws Exception {
        KVServer server = new KVServer(0, 5, "LRU");
        server.run();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        KVClient client = new KVClient();
        client.newConnection("localhost", server.getPort());
        client.getStore();
        assertEquals(KVMessage.StatusType.PUT_SUCCESS,client.getStore().put("test","test string").getStatus());

        server.clearStorage();

        assertEquals(KVMessage.StatusType.PUT_SUCCESS,client.getStore().put("test","test string").getStatus());
        KVMessage msg = client.getStore().get("test");
        assertEquals("test string",msg.getValue());
        assertEquals(KVMessage.StatusType.GET_SUCCESS,msg.getStatus());

        server.clearStorage();

        msg = client.getStore().put("test","string");
        assertEquals(KVMessage.StatusType.PUT_SUCCESS,msg.getStatus());
        msg = client.getStore().put("test","");
        assertEquals(KVMessage.StatusType.DELETE_SUCCESS,msg.getStatus());
        server.clearStorage();

        msg = client.getStore().put("test t","string");
        assertEquals(KVMessage.StatusType.PUT_ERROR,msg.getStatus());
        server.clearStorage();

        msg = client.getStore().put("012345678901234567890","string");
        assertEquals(KVMessage.StatusType.PUT_ERROR,msg.getStatus());
        server.clearStorage();

        client.getStore().disconnect();
        server.close();
    }
}
