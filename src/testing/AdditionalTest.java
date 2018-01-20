package testing;

import org.junit.Test;

import junit.framework.TestCase;


public class AdditionalTest extends TestCase {
	
	// TODO add your test cases, at least 3
	private KVJSONMessageTest KVJSONMessageTestSuit = new KVJSONMessageTest();

	private CommunicationTestSuites test;
	@Test
	public void testStub() {
        KVJSONMessageTestSuit.testKVJSONMessage_Key();
        KVJSONMessageTestSuit.testKVJSONMessage_Value();
        KVJSONMessageTestSuit.testKVJSONMessage_Serialization();
	}
}
