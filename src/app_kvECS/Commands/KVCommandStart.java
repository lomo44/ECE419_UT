package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

public class KVCommandStart extends KVCommand<ECSClient> {
    public KVCommandStart() { super(KVCommandPattern.KVCommandType.START); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        KVJSONMessage ret = new KVJSONMessage();
        try {
            if(clientInstance.start()){
                ret.setExtendStatus(eKVExtendStatusType.START_SUCCESS);
            }
            else{
                ret.setExtendStatus(eKVExtendStatusType.START_FAIL);
            }
        } catch (Exception e) {
            ret.setExtendStatus(eKVExtendStatusType.START_FAIL);
        }
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if (response.getExtendStatusType() == eKVExtendStatusType.START_SUCCESS) {
            kv_out.println_info("Successfully started up servers.");
        } else {
            kv_out.println_error("Failed to start up servers.");
        }
    }
}
