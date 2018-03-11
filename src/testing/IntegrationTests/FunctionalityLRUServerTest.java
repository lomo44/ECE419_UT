package testing.IntegrationTests;


import app_kvClient.KVClient;
import app_kvClient.Commands.*;
import app_kvServer.KVServer;
import common.KVMessage;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

import org.junit.Test;

import junit.framework.TestCase;
import testing.KVTestPortManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FunctionalityLRUServerTest extends FunctionalityFIFOServerTest {
    @Override
    protected void setUp() throws Exception{
        port = KVTestPortManager.port.incrementAndGet();
        server = new KVServer(port,10, "LRU");
        client = new KVClient(System.in);
        client.newConnection("localhost",port);
    }
}

