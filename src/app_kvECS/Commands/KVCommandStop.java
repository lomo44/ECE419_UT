package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

public class KVCommandStop extends KVCommand<ECSClient> {
    public KVCommandStop() { super(KVCommandPattern.KVCommandType.STOP); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        KVJSONMessage ret = new KVJSONMessage();
        try {
            clientInstance.stop();
            ret.setExtendStatus(eKVExtendStatusType.STOP_SUCCESS);
        } catch (Exception e) {
            ret.setExtendStatus(eKVExtendStatusType.STOP_FAIL);
        }
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if (response.getExtendStatusType() == eKVExtendStatusType.STOP_SUCCESS) {
            kv_out.println_info("Successfully stopped all servers.");
        } else {
            kv_out.println_error("Failed to stop all servers.");
        }
    }
}
