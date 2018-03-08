package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.messages.KVJSONMessage;

public class KVCommandAddNode extends KVCommand<ECSClient> {
    public KVCommandAddNode() { super(KVCommandPattern.KVCommandType.ADD_NODE); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        // TODO
        return new KVJSONMessage();
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        // TODO
        return;
    }

    public void setCacheSize(String cacheSize) { set("CacheSize",cacheSize); }
    public void setCacheStrategy(String cacheStrategy) { set("CacheStrategy",cacheStrategy); }
    public String getCacheSize() { return getValue("CacheSize"); }
    public String getCacheStrategy() { return getValue("CacheStrategy"); }
}
