package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;
import ecs.IECSNode;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class KVCommandRemoveNodeByIndex extends KVCommand<ECSClient> {
    public KVCommandRemoveNodeByIndex() { super(KVCommandPattern.KVCommandType.REMOVE_NODE); }

    public KVJSONMessage execute(ECSClient clientInstance) {
        KVJSONMessage ret = new KVJSONMessage();
        Map<String, IECSNode> nodemap = clientInstance.getNameECSNodeMap();
        int index = getIndex();
        if(index < 0 || index >= nodemap.size()){
            ret.setExtendStatus(eKVExtendStatusType.REMOVE_NODE_FAIL);
        }
        else{
            for(String name : nodemap.keySet()){
                if(index==0){
                    try {
                        clientInstance.removeNode(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        break;
                    }
                }
                else{
                    index--;
                }
            }
        }
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
