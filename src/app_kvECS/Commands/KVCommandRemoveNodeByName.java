package app_kvECS.Commands;

import app_kvECS.ECSClient;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

import java.io.IOException;

public class KVCommandRemoveNodeByName extends KVCommand<ECSClient>{
    public KVCommandRemoveNodeByName() {
        super(KVCommandPattern.KVCommandType.REMOVE_NODE_BY_NAME);
    }

    @Override
    public KVJSONMessage execute(ECSClient Instance) {
        KVJSONMessage ret = new KVJSONMessage();
        ret.setExtendStatus(eKVExtendStatusType.REMOVE_NODE_SUCCESS);
        try {
            if(!Instance.removeNode(getNodeName())){
                ret.setExtendStatus(eKVExtendStatusType.REMOVE_NODE_FAIL);
            }
        } catch (IOException e) {
            ret.setExtendStatus(eKVExtendStatusType.REMOVE_NODE_FAIL);
        }
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if(response.getExtendStatusType()==eKVExtendStatusType.REMOVE_NODE_SUCCESS){
            kv_out.println_info(String.format("%s was removed successfully\n"));
        }
        else{
            kv_out.println_info(String.format("%s was removed unsuccessfully\n"));
        }
    }

    public void setNodeName(String nodeName){
        set("node_name",nodeName);
    }
    public String getNodeName(){
        return getValue("node_name");
    }
}
