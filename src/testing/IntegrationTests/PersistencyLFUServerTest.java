package testing.IntegrationTests;

import app_kvClient.KVClient;
import testing.KVTestPortManager;

public class PersistencyLFUServerTest extends PersistencyFIFOServerTest {
    @Override
    public void setUp() throws Exception{
        cacheStratagies = "LFU";
        port = KVTestPortManager.port.incrementAndGet();
        client = new KVClient();
    }
}
