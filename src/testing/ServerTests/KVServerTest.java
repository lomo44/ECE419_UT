package testing.ServerTests;


import app_kvClient.KVClient;
import app_kvClient.testClient.KVTestClient;
import app_kvServer.KVServer;
import app_kvServer.echoServer.KVServerEcho;
import common.messages.KVMessage;
import org.junit.Test;

import junit.framework.TestCase;
import testing.KVTestPortManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class KVServerTest extends TestCase {
    private KVServerEcho serverEcho = null;
    private KVTestClient client = null;
    private int port;
    @Override
    public void setUp() throws Exception{
        port = KVTestPortManager.port.incrementAndGet();
        serverEcho = new KVServerEcho(port,10,"NULL");
        client = new KVTestClient("localhost",port);
        client.init(500);
    }

    @Override
    public void tearDown() throws Exception{
        if(serverEcho!=null){
            serverEcho.close();
            serverEcho = null;
        }
        if(client!=null){
            client.teardown();
            client = null;
        }
    }
    @Test
    public void testServerBasic_Initialization() {
        assertTrue(serverEcho.isHandlerRunning());
    }
    @Test
    public void testServerBasic_TearDown() throws InterruptedException, IOException, ClassNotFoundException {
        assertTrue(serverEcho.isHandlerRunning());
        serverEcho.close();
        assertFalse(serverEcho.isHandlerRunning());
        serverEcho = null;
    }
    @Test
    public void testServerBasic_Echo() throws InterruptedException, IOException, ClassNotFoundException {
        KVMessage newmessage = client.createKVMessage();
        newmessage.setStatus(KVMessage.StatusType.PUT);
        newmessage.setKey("foo");
        newmessage.setValue("boo");
        client.send(newmessage);
        KVMessage recievedMessage = client.get();
        assertTrue(newmessage.equal(recievedMessage));
    }
    @Test
    public void testServerBasic_Multiple_Client_Echo() throws InterruptedException, IOException, ClassNotFoundException {
        KVTestClient client1 = new KVTestClient("localhost",port);
        client1.init(0);
        KVTestClient client2 = new KVTestClient("localhost",port);
        client2.init(0);

        KVMessage newmessage1 = client1.createKVMessage();
        newmessage1.setStatus(KVMessage.StatusType.PUT);
        newmessage1.setKey("c1");
        newmessage1.setValue("c11");

        KVMessage newmessage2 = client1.createKVMessage();
        newmessage2.setStatus(KVMessage.StatusType.GET);
        newmessage2.setKey("c2");
        newmessage2.setValue("c22");

        client1.send(newmessage1);
        client2.send(newmessage2);
        KVMessage recievedMessage1 = client1.get();
        KVMessage recievedMessage2 = client2.get();
        client1.teardown();
        client2.teardown();
        assertTrue(newmessage1.equal(recievedMessage1));
        assertTrue(newmessage2.equal(recievedMessage2));
    }

}

