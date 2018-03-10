package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

public class KVCommandAddNodes extends KVCommand<ECSClient> {
    public KVCommandAddNodes() { super(KVCommandPattern.KVCommandType.ADD_NODES); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        KVJSONMessage ret = new KVJSONMessage();
        clientInstance.addNodes(Integer.parseInt(getNumNodes()),getCacheStrategy(),Integer.parseInt(getCacheSize()));
        ret.setExtendStatus(eKVExtendStatusType.ADD_NODE_SUCCESS);
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if (response.getExtendStatusType() == eKVExtendStatusType.ADD_NODE_SUCCESS) {
            kv_out.println_info("Successfully start up specified number of servers with specified cache strategy & cache size.");
        } else {
            kv_out.println_error("Failed to start up specified number of new servers.");
        }
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