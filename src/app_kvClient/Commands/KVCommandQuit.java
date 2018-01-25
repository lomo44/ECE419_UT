package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVMessage;

import java.io.IOException;

public class KVCommandQuit extends KVCommand {
    public KVCommandQuit() {
        super(KVCommandPattern.KVCommandType.QUIT);
    }

    @Override
    public KVMessage execute(KVClient clientInstance) {
        KVMessage ret = clientInstance.getStore().createEmptyMessage();
        try {
            clientInstance.stop();
            ret.setStatus(KVMessage.StatusType.DISCONNECT_SUCCESS);
        } catch (IOException e) {
            ret.setStatus(KVMessage.StatusType.DISCONNECT_FAIL);
        }
        return ret;
    }

    @Override
    public void handleResponse(KVMessage response) {
        if(response.getStatus() == KVMessage.StatusType.DISCONNECT_SUCCESS){
            kv_out.println_info("Successfully disconnected from server, quitting.");
        }
        else{
            kv_out.println_info("Failed to disconnect from server.");
        }
    }
}
