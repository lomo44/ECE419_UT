package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.messages.KVJSONMessage;

public class KVCommandRemoveNode extends KVCommand<ECSClient> {
    public KVCommandRemoveNode() { super(KVCommandPattern.KVCommandType.REMOVE_NODE); }

    public KVJSONMessage execute(ECSClient clientInstance) {
        // TODO
        return new KVJSONMessage();
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        // TODO
        return;
    }

    public void setIndex(String index) {
        set ("Index",index);
    }
    public String getIndex() {
        return getValue("Index");
    }
}
