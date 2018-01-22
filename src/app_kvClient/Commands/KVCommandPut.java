package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern.KVCommandType;
import app_kvClient.KVClient;
import common.messages.KVMessage;

public class KVCommandPut extends KVCommand {
    public KVCommandPut() {
        super(KVCommandType.PUT);
    }

    @Override
    public void execute(KVClient clientInstance) {
        KVMessage message = null;
        try {
            message = clientInstance.getStore().put(getKey(),getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(message!=null){
            clientInstance.handleMessage(message);
        }
    }

    public void setKey(String key){
        set("Key",key);
    }

    public String getKey(){
        return get("Key");
    }

    public void setValue(String value){
        set("Value",value);
    }

    public String getValue(){
        return get("Value");
    }
}
