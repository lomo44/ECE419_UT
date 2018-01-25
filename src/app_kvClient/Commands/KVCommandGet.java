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
            kv_out.println_error("No status response received.");
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
                kv_out.println_error("Key " + key + " does not exist.");
                break;
            }
            case UNKNOWN_ERROR:{
                kv_out.println_error("Unknown error.");
                break;
            }
            case NORESPONSE:{
                kv_out.println_error("No status response received.");
                break;
            }
            default:{
                kv_out.println_error("Unknown error.");
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
