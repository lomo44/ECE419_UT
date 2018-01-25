package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVMessage;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class KVCommandGet extends KVCommand {
    public KVCommandGet() {
        super(KVCommandPattern.KVCommandType.GET);
    }

    @Override
    public KVMessage execute(KVClient clientInstance) {
        KVMessage ret = clientInstance.getStore().createEmptyMessage();
        try {
            ret = clientInstance.getStore().get(getKey());
        } catch (SocketTimeoutException e) {
            // Socket Timeout, need to retry
            int i =0;
            while(i < clientInstance.getAttribute().timeoutRetryCount){
                try {
                    return clientInstance.getStore().get(getKey());
                } catch (SocketTimeoutException e1) {
                    System.out.println("Timeout, retry count: "+i);
                    i++;
                } catch (SocketException e1) {
                    ret.setStatus(KVMessage.StatusType.NORESPONSE);
                    clientInstance.disconnect();
                    break;
                }
            }
        } catch (SocketException e) {
            ret.setStatus(KVMessage.StatusType.NORESPONSE);
            clientInstance.disconnect();
        }
        finally {
            return ret;
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
            case NORESPONSE:{
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
        return get("Key");
    }
}
