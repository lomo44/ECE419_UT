package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVMessage;

public class KVCommandGet extends KVCommand {
    public KVCommandGet() {
        super(KVCommandPattern.KVCommandType.GET);
    }

    @Override
    public KVMessage execute(KVClient clientInstance) {
        try {
            return clientInstance.getStore().get(getKey());

        } catch (Exception e) {
            System.out.println("Error! No status response received!");
            e.printStackTrace();
            KVMessage newmsg = clientInstance.getStore().createEmptyMessage();
            newmsg.setStatus(KVMessage.StatusType.NORESPONSE);
            return newmsg;
        }
    }


    @Override
    public void handleResponse(KVMessage response) {
        KVMessage.StatusType statusType = response.getStatus();
        String key = response.getKey();
        String value = response.getValue();
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
            case NORESPONSE:{
                System.out.println("No response! ");
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
}
