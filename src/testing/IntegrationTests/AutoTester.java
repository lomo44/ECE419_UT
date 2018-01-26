package testing.IntegrationTests;

import app_kvClient.KVClient;
import app_kvServer.KVServer;
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
}
