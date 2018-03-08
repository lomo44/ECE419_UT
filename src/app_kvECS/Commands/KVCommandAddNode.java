package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;

public class KVCommandAddNode extends KVCommand<ECSClient> {

    public void execute(ECSClient clientInstance) {
        // TODO
        return;
    }

    public void setCacheSize(String cacheSize) { set("CacheSize",cacheSize); }
    public void setCacheStrategy(String cacheStrategy) { set("CacheStrategy",cacheStrategy); }
    public String getCacheSize() { return getValue("CacheSize"); }
    public String getCacheStrategy() { return getValue("CacheStrategy"); }
}
