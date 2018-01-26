package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

import java.io.IOException;

public class KVCommandDisconnect extends KVCommand {
    public KVCommandDisconnect() {
        super(KVCommandPattern.KVCommandType.DISCONNECT);
    }

    @Override
    public KVJSONMessage execute(KVClient clientInstance) {
        KVJSONMessage ret = clientInstance.getStore().createEmptyMessage();
        try {
            clientInstance.disconnect();
            ret.setExtendStatus(eKVExtendStatusType.DISCONNECT_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            ret.setExtendStatus(eKVExtendStatusType.DISCONNECT_FAIL);
        }
        finally {
            return ret;
        }
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if(response.getExtendStatusType() == eKVExtendStatusType.DISCONNECT_SUCCESS){
            kv_out.println_info("Successfully disconnected from server.");
        }
        else{
            kv_out.println_error("Failed to disconnect from server.");
        }
    }
}
