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

public class PersistencyLRUServerTest extends PersistencyFIFOServerTest {
    @Override
    public void setUp() throws Exception{
        cacheStratagies = "LFU";
        port = KVTestPortManager.port.incrementAndGet();
        client = new KVClient(System.in);
    }
}
