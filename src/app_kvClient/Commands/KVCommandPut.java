package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern.KVCommandType;
import app_kvClient.KVClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

public class KVCommandPut extends KVCommand {
    public KVCommandPut() {
        super(KVCommandType.PUT);
    }

    @Override
    public KVJSONMessage execute(KVClient clientInstance) {
        KVJSONMessage ret  = null;
        try {
            ret = (KVJSONMessage)clientInstance.getStore().put(getKey(),getValue());
        } catch (Exception e) {
            kv_out.println_error("No status response received.");
            e.printStackTrace();
            ret = clientInstance.getStore().createEmptyMessage();
            ret.setExtendStatus(eKVExtendStatusType.NO_RESPONSE);
        }
        finally {
            return ret;
        }
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
       eKVExtendStatusType statusType = response.getExtendStatusType();
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
            case NO_RESPONSE:{
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
