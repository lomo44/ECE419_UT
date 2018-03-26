package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

public class KVCommandShutdown extends KVCommand<ECSClient> {
    public KVCommandShutdown() { super(KVCommandPattern.KVCommandType.SHUT_DOWN); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        KVJSONMessage ret = new KVJSONMessage();
        try {
            if(clientInstance.shutdown()){
                ret.setExtendStatus(eKVExtendStatusType.SHUTDOWN_SUCCESS);
            }
            else{
                ret.setExtendStatus(eKVExtendStatusType.SHUTDOWN_FAIL);
            }
        } catch (Exception e) {
            ret.setExtendStatus(eKVExtendStatusType.SHUTDOWN_FAIL);
        }
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if (response.getExtendStatusType() == eKVExtendStatusType.SHUTDOWN_SUCCESS) {
            kv_out.println_info("Successfully shut down active servers.");
        } else {
            kv_out.println_error("Failed to shut down active servers. Server data has not been fully cleaned");
        }
    }
}
