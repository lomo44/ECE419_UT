package testing.CommunicationTests;

import app_kvClient.KVClient;
import app_kvServer.KVServer;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class KVMigrationModuleTest extends TestCase {
    public Vector<KVServer> serverList;
    public Vector<KVClient> clientList;


    public int numOfClientServerPair = 10;
    public int msgPerServer = 7;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testKVMigrationModule_Basic(){


    }
}
