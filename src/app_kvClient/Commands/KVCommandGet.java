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
            KVMessage.StatusType statusType = message.getStatus();
            String key = message.getKey();
            String value = message.getValue();
            switch(statusType) {
                case GET_SUCCESS:{
                    System.out.println(value);
                }
                case GET_ERROR:{
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
    }

    public void setKey(String key){
        set("Key",key);
    }

    public String getKey(){
        return get("Key");
    }
}
