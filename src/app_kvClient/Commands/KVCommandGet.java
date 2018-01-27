package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;
import client.KVStore;

import java.net.SocketException;
import java.net.SocketTimeoutException;

public class KVCommandGet extends KVCommand {
    public KVCommandGet() {
        super(KVCommandPattern.KVCommandType.GET);
    }

    @Override
    public KVJSONMessage execute(KVClient clientInstance) {
        KVJSONMessage ret = KVStore.createEmptyMessage();
        if (clientInstance.getStore() == null) {
            ret.setExtendStatus(eKVExtendStatusType.NO_RESPONSE);
            return ret;
        }
        try {
            ret = (KVJSONMessage) clientInstance.getStore().get(getKey());
        } catch (SocketTimeoutException e) {
            // Socket Timeout, need to retry
            int i =0;
            while(i < clientInstance.getAttribute().timeoutRetryCount){
                try {
                    ret = (KVJSONMessage)clientInstance.getStore().get(getKey());
                    break;
                } catch (SocketTimeoutException e1) {
                    System.out.println("Timeout, retry count: "+i);
                    i++;
                } catch (SocketException e1) {
                    ret.setExtendStatus(eKVExtendStatusType.NO_RESPONSE);
                    clientInstance.disconnect();
                    break;
                }
            }
        } catch (SocketException e) {
            ret.setExtendStatus(eKVExtendStatusType.NO_RESPONSE);
            clientInstance.disconnect();
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
        switch(statusType) {
            case GET_SUCCESS:{
                System.out.println(value);
                break;
            }
            case GET_ERROR:{
                kv_out.println_error("Key " + key + " does not exist.");
                break;
            }
            case UNKNOWN_ERROR:{
                kv_out.println_error("Unknown error.");
                break;
            }
            case NO_RESPONSE:{
                kv_out.println_error("No status response received.");
                break;
            }
            default:{
                kv_out.println_error("Unknown error.");
            }
        }
    }

    public void setKey(String key){
        set("Key",key);
    }

    public String getKey(){
        return getValue("Key");
    }
}
