package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;

public class KVCommandRemoveNode extends KVCommand<ECSClient> {

    public void execute(ECSClient clientInstance) {
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
