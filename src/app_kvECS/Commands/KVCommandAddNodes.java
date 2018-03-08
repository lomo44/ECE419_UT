package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;

public class KVCommandAddNodes extends KVCommand<ECSClient> {

    public void execute(ECSClient clientInstance) {
        // TODO
        return;
    }

    public void setNumNodes(String numNodes) {
        set("NumNodes",numNodes);
    }
    public void setCacheSize(String cacheSize) {
        set("CacheSize",cacheSize);
    }
    public void setCacheStrategy(String cacheStrategy) {
        set("CacheStrategy",cacheStrategy);
    }
    public String getNumNodes() {
        return getValue("NumNodes");
    }
    public String getCacheSize() {
        return getValue("CacheSize");
    }
    public String getCacheStrategy() {
        return getValue("CacheStrategy");
    }
}
