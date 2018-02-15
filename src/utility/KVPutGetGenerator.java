package utility;

import common.command.KVCommand;
import app_kvClient.Commands.KVCommandGet;
import app_kvClient.Commands.KVCommandPut;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;

public class KVPutGetGenerator implements IKVTrafficGenerator {
    private int putPercentage = 50;
    private int overwritePercentage = 50;
    private int keyLength = 10;
    private HashMap<String, String> dataContent;
    private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()";


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

    private String getRandomeString(int length){
        // Clean previous build string
        StringBuilder stringBuilder = new StringBuilder();
        while(stringBuilder.length() < length){
            stringBuilder.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(0,alphabet.length())));
        }
        return  stringBuilder.toString();
    }

    private int getRandomPercentage(){
        return ThreadLocalRandom.current().nextInt(0,100);
    }

    private KVCommandGet generateGet(){
        // Generate getValue command
        KVCommandGet commandGet = new KVCommandGet();
        commandGet.setKey(getRandomKeyFromContent());
        return commandGet;
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
        return "";
    }

    private KVCommandPut generatePut(){
        KVCommandPut ret = new KVCommandPut();
        if(getRandomPercentage()<overwritePercentage && this.dataContent.size()!=0){
            // Generate overwrite command
            ret.setValue(getRandomeString(keyLength));
            ret.setKey(getRandomKeyFromContent());
        }
        else{
            // Generate new put command
            ret.setValue(getRandomeString(keyLength));
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
}
