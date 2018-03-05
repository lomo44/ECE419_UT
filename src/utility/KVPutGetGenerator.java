package utility;

import common.command.KVCommand;
import app_kvClient.Commands.KVCommandGet;
import app_kvClient.Commands.KVCommandPut;
import common.datastructure.Pair;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;

public class KVPutGetGenerator extends IKVTrafficGenerator {
    private int putPercentage = 50;
    private int overwritePercentage = 50;
    private int keyLength = 10;
    private int payloadLength = 10;
    private HashMap<String, String> dataContent;



    public KVPutGetGenerator(int putPercentage, int overwritePercentage){
        dataContent = new HashMap<>();
        this.putPercentage = putPercentage;
        this.overwritePercentage = overwritePercentage;
    }
    void setPutPercentage(int percentage){
        putPercentage = min(percentage,100);
    }
    void setOverWritePercentage(int percentage){
        overwritePercentage = min(percentage,100);
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public void setPayloadLength(int payloadLength) {
        this.payloadLength = payloadLength;
    }

    public KVCommandGet generateGet(){
        // Generate getValue command
        KVCommandGet commandGet = new KVCommandGet();
        String key = getRandomKeyFromContent();
        if(key!=null){
            commandGet.setKey(key);
            return commandGet;
        }
        return null;
    }

    private String getRandomKeyFromContent(){
        int keycount = dataContent.keySet().size();
        int keyindex = ThreadLocalRandom.current().nextInt(0,keycount);
        int keyCounter = 0;
        for(String str : dataContent.keySet()){
            if(keyCounter == keyindex){
                return str;
            }
            keyCounter++;
        }
        return null;
    }

    public KVCommandPut generatePut(){
        KVCommandPut ret = new KVCommandPut();
        if(getRandomPercentage()<overwritePercentage && this.dataContent.size()!=0){
            // Generate overwrite command
            ret.setValue(getRandomeString(payloadLength));
            ret.setKey(getRandomKeyFromContent());
        }
        else{
            // Generate new put command
            ret.setValue(getRandomeString(payloadLength));
            ret.setKey(getRandomeString(keyLength));
        }
        dataContent.put(ret.getKey(),ret.getValue());
        return ret;
    }


    @Override
    public KVCommand getNextCommand() {
        if(this.dataContent.size()!=0){
            if(getRandomPercentage() < putPercentage){
                return generatePut();
            }
            else{
                return generateGet();
            }
        }
        else{
            return generatePut();
        }
    }

    public boolean verify(String key, String value){
        if(dataContent.containsKey(key)){
            if(dataContent.get(key).equals(value)){
                return true;
            }
        }
        return false;
    }

    public Pair<String,String> getRandomEntry(){
        return new Pair<>(getRandomeString(keyLength),getRandomeString(payloadLength));
    }
}
