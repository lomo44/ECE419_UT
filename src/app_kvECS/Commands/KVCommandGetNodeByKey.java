package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.messages.KVJSONMessage;

public class KVCommandGetNodeByKey extends KVCommand<ECSClient> {
    public KVCommandGetNodeByKey() { super(KVCommandPattern.KVCommandType.GET_NODE_BY_KEY); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        // TODO
        System.out.println(clientInstance.getNodeByKey(getKey()).getNodeName());
        return new KVJSONMessage();
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        // TODO
        return;
    }

    public void setKey(String key){set("key",key);}
    public String getKey(){return getValue("key");}
}
