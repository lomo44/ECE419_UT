package testing;

import org.junit.Test;

import junit.framework.TestCase;
import testing.CommunicationTests.KVJSONMessageTest;


public class AdditionalTest extends TestCase {

	private KVJSONMessageTest kvjsonMessageTest = new KVJSONMessageTest();
	@Test
	public void testStub() {
		kvjsonMessageTest.run();
	}
}
