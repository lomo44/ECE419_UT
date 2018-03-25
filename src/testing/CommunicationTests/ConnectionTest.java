package testing.CommunicationTests;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import app_kvServer.KVServer;
import client.KVStore;

import junit.framework.TestCase;
import logger.LogSetup;
import org.apache.log4j.Level;
import org.junit.Test;
import testing.KVTestPortManager;


public class ConnectionTest extends TestCase {

	private KVServer server = null;
	private int port = 0;

	@Override
	public void setUp() throws Exception{
		new LogSetup("logs/testing/test.log", Level.ERROR);
		port = KVTestPortManager.port.incrementAndGet();
		server =  new KVServer(port, 10, "FIFO");
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
		KVStore kvClient = new KVStore("localhost", port);
		try {
			kvClient.connect();
		} catch (Exception e) {
			ex = e;
		}
		assertNull(ex);
	}
	
	@Test
	public void testConnectionTimeOut(){
		Exception ex = null;
		KVStore kvClient = new KVStore("localhost", port);
		try {
		kvClient.connect();
		kvClient.testtimeout();
		} catch (Exception e) {
			ex = e;
		}
		assertSame(SocketTimeoutException.class, ex.getClass());
	}
	
	@Test
	public void testUnknownHost() {
		Exception ex = null;
		KVStore kvClient = new KVStore("unknown123", port);
		
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

