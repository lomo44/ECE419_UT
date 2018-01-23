package testing;


import app_kvClient.testClient.KVTestClient;
import app_kvServer.KVServer;
import app_kvServer.echoServer.KVServerEcho;
import common.messages.KVMessage;
import org.junit.Test;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class KVServerTest extends TestCase {
    @Test
    public void testServerBasic_Initialization() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(50001,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(serverEcho.isHandlerRunning());
        serverEcho.close();
    }
    @Test
    public void testServerBasic_TearDown() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(50002,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(serverEcho.isHandlerRunning());
        serverEcho.close();
        TimeUnit.SECONDS.sleep(1);
        assertFalse(serverEcho.isHandlerRunning());
    }
    @Test
    public void testServerBasic_Echo() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(50003,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        KVTestClient client = new KVTestClient("localhost",50003);
        client.init(0);
        KVMessage newmessage = client.createKVMessage();
        newmessage.setStatus(KVMessage.StatusType.PUT);
        newmessage.setKey("foo");
        newmessage.setValue("boo");
        client.send(newmessage);
        KVMessage recievedMessage = client.get();
        serverEcho.close();
        client.teardown();
        //boolean a = newmessage.equal(recievedMessage);
        assertTrue(newmessage.equal(recievedMessage));
    }
    @Test
    public void testServerBasic_Mutiple_Clinet_Echo() throws InterruptedException, IOException, ClassNotFoundException {
        KVServerEcho serverEcho = new KVServerEcho(50004,10,"NULL");
        TimeUnit.SECONDS.sleep(1);
        KVTestClient client1 = new KVTestClient("localhost",50004);
        client1.init(0);
        KVTestClient client2 = new KVTestClient("localhost",50004);
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
        serverEcho.close();
        client1.teardown();
        client2.teardown();
        assertTrue(newmessage1.equal(recievedMessage1));
        assertTrue(newmessage2.equal(recievedMessage2));
    }

}

