package testing.CommunicationTests;


import common.messages.KVMessage;
import org.junit.Test;

import junit.framework.TestCase;
import common.messages.KVJSONMessage;


public class KVJSONMessageTest extends TestCase {
    @Test
    public void testKVJSONMessage_Key(){
        KVJSONMessage message = new KVJSONMessage();
        message.setKey("foo");
        assertEquals(message.getKey(),"foo");
    }
    @Test
    public void testKVJSONMessage_Value(){
        KVJSONMessage message = new KVJSONMessage();
        message.setValue("boo");
        assertEquals(message.getValue(),"boo");
    }
    @Test
    public void testKVJSONMessage_Serialization(){
        KVJSONMessage message = new KVJSONMessage();
        message.setValue("boo");
        message.setKey("foo");
        message.setStatus(KVMessage.StatusType.PUT);
        byte[] bytearray = message.toBytes();
        KVJSONMessage newmessage = new KVJSONMessage();
        newmessage.fromBytes(bytearray,0,bytearray.length);
        assertEquals(newmessage.getKey(),"foo");
        assertEquals(newmessage.getValue(),"boo");
    }

}
