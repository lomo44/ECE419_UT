package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVJSONMessage;
import common.messages.KVMessage;

import java.io.IOException;

public class KVCommandDisconnect extends KVCommand {
    public KVCommandDisconnect() {
        super(KVCommandPattern.KVCommandType.DISCONNECT);
    }

    @Override
    public KVMessage execute(KVClient clientInstance) {
        KVJSONMessage ret = clientInstance.getStore().createEmptyMessage();
        try {
            clientInstance.disconnect();
            ret.setStatus(KVMessage.StatusType.DISCONNECT_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            ret.setStatus(KVMessage.StatusType.DISCONNECT_FAIL);
        }
        finally {
            return ret;
        }
    }

    @Override
    public void handleResponse(KVMessage response) {
        if(response.getStatus() == KVMessage.StatusType.DISCONNECT_SUCCESS){
            kv_out.println_info("Successfully disconnected from server.");
        }
        else{
            kv_out.println_error("Failed to disconnect from server.");
        }
    }
}
