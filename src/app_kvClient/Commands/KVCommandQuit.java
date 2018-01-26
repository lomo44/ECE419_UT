package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

import java.io.IOException;

public class KVCommandQuit extends KVCommand {
    public KVCommandQuit() {
        super(KVCommandPattern.KVCommandType.QUIT);
    }

    @Override
    public KVJSONMessage execute(KVClient clientInstance) {
        KVJSONMessage ret = new KVJSONMessage();
        try {
            clientInstance.stop();
            ret.setExtendStatus(eKVExtendStatusType.DISCONNECT_SUCCESS);
        } catch (IOException e) {
            ret.setExtendStatus(eKVExtendStatusType.DISCONNECT_FAIL);
        }
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if(response.getExtendStatusType() == eKVExtendStatusType.DISCONNECT_SUCCESS){
            kv_out.println_info("Successfully disconnected from server, quitting.");
        }
        else{
            kv_out.println_info("Failed to disconnect from server.");
        }
    }
}
