package testing.IntegrationTests;

import app_kvClient.KVClient;
import app_kvServer.KVServer;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import common.enums.eKVLogLevel;
import common.messages.KVJSONMessage;
import common.messages.KVMessage;
import junit.framework.TestCase;
import org.junit.Test;
import utility.KVPutGetGenerator;

public class LargeFileTranmissionTest extends TestCase{
    protected KVServer server = null;
    protected KVClient client = null;
    protected int port;
    protected KVPutGetGenerator generator;


    @Override
    protected void setUp() throws Exception{
        server = new KVServer(0, 10, "FIFO");
        server.run();
        server.setLogLevel(eKVLogLevel.ALL,eKVLogLevel.ALL);
        client = new KVClient(System.in);
        port =server.getPort();
        client.newConnection("localhost",port);
        client.setLogLevel(eKVLogLevel.ALL,eKVLogLevel.ALL);
        generator = new KVPutGetGenerator(50,50);
        generator.setKeyLength(10);
        generator.setPayloadLength(25000);
    }

    @Override
    protected void tearDown() throws Exception{
        client.disconnect();
        server.clearStorage();
        server.close();
        server = null;
        client = null;
    }

    @Test
    public void testClientBasic_PutSuccess(){
        assertTrue(client.isConnected());
        assertTrue(server.isHandlerRunning());
        int i = 0;
        while(i < 10){
            KVCommand cmd = generator.getNextCommand();
            KVJSONMessage msg = client.executeCommand(cmd);
            if(cmd.getCommandType()== KVCommandPattern.KVCommandType.GET){
                //System.out.println("Generate Get");
                assertEquals(KVMessage.StatusType.GET_SUCCESS,msg.getStatus());
                assertEquals(true,generator.verify(msg.getKey(),msg.getValue()));
            }
            if(cmd.getCommandType()== KVCommandPattern.KVCommandType.PUT){
                //System.out.println("Generate Puts");
                assertTrue(msg.getStatus()== KVMessage.StatusType.PUT_SUCCESS || msg.getStatus() == KVMessage.StatusType.PUT_UPDATE);
            }
            i++;
        }
    }
}
