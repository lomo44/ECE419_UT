package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern.KVCommandType;
import app_kvClient.KVClient;
import common.messages.KVJSONMessage;
import common.messages.KVMessage;

public class KVCommandPut extends KVCommand {
    public KVCommandPut() {
        super(KVCommandType.PUT);
    }

    @Override
    public KVMessage execute(KVClient clientInstance) {
        try {
            return clientInstance.getStore().put(getKey(),getValue());
        } catch (Exception e) {
            kv_out.println_error("No status response received.");
            e.printStackTrace();
            KVJSONMessage newmsg = clientInstance.getStore().createEmptyMessage();
            newmsg.setStatus(KVMessage.StatusType.NORESPONSE);
            return newmsg;
        }
    }

    @Override
    public void handleResponse(KVMessage response) {
        KVMessage.StatusType statusType = response.getStatus();
        String key = response.getKey();
        String value = response.getValue();
        switch (statusType) {
            case PUT_SUCCESS:{
                kv_out.println_info("Successful PUT request.");
                break;
            }
            case PUT_UPDATE:{
                kv_out.println_info("Successful PUT update.");
                break;
            }
            case PUT_ERROR:{
                kv_out.println_error("Failed PUT request.");
                break;
            }
            case UNKNOWN_ERROR:{
                kv_out.println_error("Unknown error.");
                break;
            }
            case NORESPONSE:{
                kv_out.println_debug("No response.");
                break;
            }
            default:{
                kv_out.println_debug("Unknown error.");
                break;
            }
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
