package testing.IntegrationTests;


import app_kvClient.Commands.KVCommandEcho;
import app_kvClient.Commands.KVCommandGet;
import app_kvClient.Commands.KVCommandPut;
import app_kvClient.KVClient;
import app_kvServer.KVServer;
import common.messages.KVMessage;
import junit.framework.TestCase;
import org.junit.Test;
import testing.KVTestPortManager;

public class FunctionalityLFUServerTest extends FunctionalityFIFOServerTest {

    @Override
    protected void setUp() throws Exception{
        port = KVTestPortManager.port.incrementAndGet();
        server = new KVServer(port, 10, "LRU");
        client = new KVClient();
        client.newConnection("localhost",port);
    }
}

