package app_kvClient.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvClient.KVClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;
import client.KVStore;

import java.net.SocketException;

public class KVCommandGet extends KVCommand<KVClient> {
    public KVCommandGet() {
        super(KVCommandPattern.KVCommandType.GET);
    }

    @Override
    public KVJSONMessage execute(KVClient clientInstance) {
        KVJSONMessage ret = KVStore.createEmptyMessage();
        if (clientInstance.getStore() == null) {
            kv_out.println_error("Client not connected to a server.");
            ret.setExtendStatus(eKVExtendStatusType.NO_RESPONSE);
            return ret;
        }
        try {
            ret = (KVJSONMessage) clientInstance.getStore().get(getKey());
        }
        catch (SocketException e) {
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
            case SERVER_WRITE_LOCK:{
                System.out.println(value);
                break;
            }
            case SERVER_STOPPED:{
                kv_out.println_error("Server stopped.");
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
