package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.messages.KVJSONMessage;
import ecs.IECSNode;

import java.util.Map;

public class KVCommandGetNodes extends KVCommand<ECSClient> {
    public KVCommandGetNodes() { super(KVCommandPattern.KVCommandType.GET_NODES); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        // TODO
        Map<String, IECSNode> map = clientInstance.getNodes();
        int count = 0;
        for(String key : map.keySet()){
            IECSNode node = map.get(key);
            System.out.println(String.format("Index: %d: Name: %s, Host: %s, Port: %d",
                    count,node.getNodeName(),node.getNodeHost(),node.getNodePort()));
            count++;
        }
        return new KVJSONMessage();
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        // TODO
        return;
    }
}
