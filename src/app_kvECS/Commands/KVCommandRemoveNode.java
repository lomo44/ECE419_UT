package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

import java.util.Collection;

public class KVCommandRemoveNode extends KVCommand<ECSClient> {
    public KVCommandRemoveNode() { super(KVCommandPattern.KVCommandType.REMOVE_NODE); }

    public KVJSONMessage execute(ECSClient clientInstance) {
        KVJSONMessage ret = new KVJSONMessage();
        //clientInstance.removeNode(Integer.parseInt(getIndex()));
        ret.setExtendStatus(eKVExtendStatusType.REMOVE_NODE_SUCCESS);
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if (response.getExtendStatusType() == eKVExtendStatusType.REMOVE_NODE_SUCCESS) {
            kv_out.println_info("Successfully deleted server by index.");
        } else {
            kv_out.println_error("Failed to delete server by index.");
        }
    }

    public void setIndex(String index) {
        set ("Index",index);
    }
    public int getIndex() {
        return Integer.parseInt(getValue("Index"));
    }
}
