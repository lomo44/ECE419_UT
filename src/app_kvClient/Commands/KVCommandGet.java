package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVMessage;

public class KVCommandGet extends KVCommand {
    public KVCommandGet() {
        super(KVCommandPattern.KVCommandType.GET);
    }

    @Override
    public void execute(KVClient clientInstance) {
        KVMessage message = null;
        try {
            message = clientInstance.getStore().get(getKey());

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
}
