package testing.PerformanceTest.PerformanceEvaluation_PutGet;

import common.command.KVCommandPattern;
import common.KVMessage;
import common.command.KVCommand;
import app_kvClient.Commands.KVCommandGet;
import app_kvClient.Commands.KVCommandPut;
import app_kvClient.KVClient;
import app_kvServer.KVServer;
import common.enums.eKVLogLevel;
import common.messages.KVJSONMessage;
import junit.framework.TestCase;
import org.junit.Test;
import utility.KVPutGetGenerator;

public class PutGetEvaluation extends TestCase {
    private KVServer server = null;
    private KVClient client = null;
    private KVPutGetGenerator generator = null;
    private int counter = 20;
    private String cacheType = "";
    private int putPercentage;

    protected String setCacheType(){
        return "FIFO";
    }

    protected int setCacheSize(){
        return 100;
    }

    protected int setCommandCount(){
        return 200;
    }

    protected int setPutPercentage(){
        return 50;
    }

    protected int setOverWritePercentage(){
        return 50;
    }

    private long calculateMsgSize(String key, String value){
        KVJSONMessage msg = client.getStore().createEmptyMessage();
        msg.setKey(key);
        msg.setStatus(KVMessage.StatusType.PUT_SUCCESS);
        msg.setValue(value);
        return msg.toBytes().length;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        putPercentage = setPutPercentage();
        cacheType = setCacheType();
        System.out.printf("Performance Evaluation Put Get: Cache Type: %s, Put Percentage: %d\n",cacheType,putPercentage);
        // Initialize server
        server = new KVServer(0,setCacheSize(),cacheType);
        // setup client
        client = new KVClient();
        client.newConnection("localhost",server.getPort());
        cacheType = setCacheType();
        putPercentage = setPutPercentage();
        generator = new KVPutGetGenerator(putPercentage,setOverWritePercentage());
        this.counter = setCommandCount();
        server.setLogLevel(eKVLogLevel.OFF,eKVLogLevel.OFF);
        client.setLogLevel(eKVLogLevel.OFF,eKVLogLevel.OFF);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(server!=null){
            server.clearStorage();
            server.close();
            server = null;
        }
        if(client!=null){
            client.stop();
            client = null;
        }
    }

    @Test
    public void testInitialization(){
        KVCommand cmd = generator.getNextCommand();
        KVJSONMessage msg = client.executeCommand(cmd);
        if(cmd.getCommandType()== KVCommandPattern.KVCommandType.GET){
            assertEquals(KVMessage.StatusType.GET_SUCCESS,msg.getStatus());
        }
        if(cmd.getCommandType()== KVCommandPattern.KVCommandType.PUT){
            assertTrue(msg.getStatus()== KVMessage.StatusType.PUT_SUCCESS || msg.getStatus() == KVMessage.StatusType.PUT_UPDATE);
        }
    }



    @Test
    public void testPerformance(){
        int i = 0;
        double averageRTT = 0;
        double averageDataSize = 0;
        int put_count = 0;
        int get_count = 0;
        while(i < counter){
            KVCommand cmd = generator.getNextCommand();
            KVJSONMessage msg = client.executeCommand(cmd);
            if(cmd.getCommandType()== KVCommandPattern.KVCommandType.GET){
                get_count+=1;
                averageDataSize += calculateMsgSize(((KVCommandGet)(cmd)).getKey(),"");
                assertEquals(KVMessage.StatusType.GET_SUCCESS,msg.getStatus());
            }
            if(cmd.getCommandType()== KVCommandPattern.KVCommandType.PUT){
                put_count+=1;
                averageDataSize += calculateMsgSize(((KVCommandPut)(cmd)).getKey(),((KVCommandPut)(cmd)).getValue());
                assertTrue(msg.getStatus()== KVMessage.StatusType.PUT_SUCCESS || msg.getStatus() == KVMessage.StatusType.PUT_UPDATE);
            }
            averageRTT += System.currentTimeMillis()-msg.getSendTime();

            i++;
        }
        averageRTT/=counter;
        averageDataSize/=counter;
        double tp = averageDataSize/averageRTT * 1000.0;
        System.out.printf("Total number of command: %d\n",counter);
        System.out.printf("Put Count: %d, Get Count: %d\n",put_count,get_count);
        System.out.printf("Average rtt: %f\n", averageRTT);
        System.out.printf("Average data size: %f\n", averageDataSize);
        System.out.printf("Throughput : %.2f KB/S\n",tp);
        //System.out.printf("Total duration: %d\n", (System.currentTimeMillis()-startTime)/1000);
    }
}
