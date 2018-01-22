package testing;

import org.junit.Test;

import junit.framework.TestCase;


public class AdditionalTest extends TestCase {
	
	// TODO add your test cases, at least 3
	private KVJSONMessageTest KVJSONMessageTestSuit = new KVJSONMessageTest();

	private KVJSONMessageTest kvjsonMessageTest = new KVJSONMessageTest();
	@Test
	public void testStub() {
		kvjsonMessageTest.run();
	}
}
