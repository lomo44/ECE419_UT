package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern.KVCommandType;
import app_kvClient.KVClient;
import common.messages.KVMessage;

public class KVCommandPut extends KVCommand {
    public KVCommandPut() {
        super(KVCommandType.PUT);
    }

    @Override
    public KVMessage execute(KVClient clientInstance) {
        KVMessage message = null;
        try {
            return clientInstance.getStore().put(getKey(),getValue());
        } catch (Exception e) {
            System.out.println("Error! No status response received!");
            e.printStackTrace();
        }
    }

    @Override
    public void handleResponse(KVMessage response) {
        KVMessage.StatusType statusType = message.getStatus();
        String key = message.getKey();
        String value = message.getValue();
        switch (statusType) {
            case PUT_SUCCESS:{
                System.out.println("Success!");
            }
            case PUT_UPDATE:{
                System.out.println("Success (update)!");
            }
            case PUT_ERROR:{
                System.out.println("Error! Key " + key + " does not exist!");
            }
            case UNKNOWN_ERROR:{
                System.out.println("Error! " + value);
            }
            default:{
                System.out.println("Error! " + value);
            }
        }
        printPrompt();
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
