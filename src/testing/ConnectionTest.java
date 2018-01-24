package testing;

import java.io.IOException;
import java.net.UnknownHostException;

import app_kvServer.KVServer;
import client.KVStore;

import junit.framework.TestCase;
import logger.LogSetup;
import org.apache.log4j.Level;
import org.junit.Test;


public class ConnectionTest extends TestCase {

	private KVServer server = null;

	@Override
	public void setUp() throws Exception{
		new LogSetup("logs/testing/test.log", Level.ERROR);
		server =  new KVServer(50000, 10, "FIFO");
	}

	@Override
	public void tearDown() throws Exception{
		server.clearStorage();
		server.close();
		server = null;
	}

	@Test
	public void testConnectionSuccess() {
		
		Exception ex = null;
		KVStore kvClient = new KVStore("localhost", 50000);
		try {
			kvClient.connect();
		} catch (Exception e) {
			ex = e;
		}
		assertNull(ex);
	}
	
	@Test
	public void testUnknownHost() {
		Exception ex = null;
		KVStore kvClient = new KVStore("unknown", 50000);
		
		try {
			kvClient.connect();
		} catch (Exception e) {
			ex = e; 
		}
		
		assertTrue(ex instanceof UnknownHostException);
	}
	
	@Test
	public void testIllegalPort() {
		Exception ex = null;
		KVStore kvClient = new KVStore("localhost", 123456789);
		
		try {
			kvClient.connect();
		} catch (Exception e) {
			ex = e; 
		}
		
		assertTrue(ex instanceof IllegalArgumentException);
	}
	
	

	
}

