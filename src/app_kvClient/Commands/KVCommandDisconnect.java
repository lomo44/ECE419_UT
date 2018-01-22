package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVMessage;

import java.io.IOException;

public class KVCommandDisconnect extends KVCommand {
    public KVCommandDisconnect() {
        super(KVCommandPattern.KVCommandType.DISCONNECT);
    }

    @Override
    public KVMessage execute(KVClient clientInstance) {
        KVMessage ret = clientInstance.getStore().createEmptyMessage();
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
            System.out.println("Successfully disconnect");
        }
        else{
            System.out.println("Failed to disconnect");
        }
    }
}
