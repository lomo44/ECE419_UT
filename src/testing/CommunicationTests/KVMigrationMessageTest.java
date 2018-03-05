package testing.CommunicationTests;

import common.messages.KVJSONMessage;
import common.messages.KVMigrationMessage;
import junit.framework.TestCase;
import org.junit.Test;

public class KVMigrationMessageTest extends TestCase {
    @Test
    public void testKVMigrationMessage_add(){
        KVMigrationMessage msg = new KVMigrationMessage();
        msg.add("123","456");
        assertEquals(msg.get("123"),"456");
    }

    @Test
    public void testKVMigrationMessage_serialization(){
        KVMigrationMessage msg = new KVMigrationMessage();
        for(int i = 0; i < 26; i++){
            msg.add(Integer.toString(i),Integer.toString(i+3));
        }
        KVJSONMessage kvjsonMessage = msg.toKVJSONMessage();
        KVMigrationMessage newMsg = KVMigrationMessage.fromKVJSONMessage(kvjsonMessage);
        assertEquals(newMsg.getEntries(),msg.getEntries());
    }
}
