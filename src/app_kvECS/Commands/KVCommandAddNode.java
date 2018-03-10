package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

public class KVCommandAddNode extends KVCommand<ECSClient> {
    public KVCommandAddNode() { super(KVCommandPattern.KVCommandType.ADD_NODE); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        KVJSONMessage ret = new KVJSONMessage();
        clientInstance.addNode(getCacheStrategy(),Integer.parseInt(getCacheSize()));
        ret.setExtendStatus(eKVExtendStatusType.ADD_NODE_SUCCESS);
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if (response.getExtendStatusType() == eKVExtendStatusType.ADD_NODE_SUCCESS) {
            kv_out.println_info("Successfully start up new server with specified cache strategy & cache size.");
        } else {
            kv_out.println_error("Failed to start up new server.");
        }
    }

    public void setCacheSize(String cacheSize) { set("CacheSize",cacheSize); }
    public void setCacheStrategy(String cacheStrategy) { set("CacheStrategy",cacheStrategy); }
    public String getCacheSize() { return getValue("CacheSize"); }
    public String getCacheStrategy() { return getValue("CacheStrategy"); }
}
