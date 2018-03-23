package app_kvECS.Commands;

import app_kvECS.ECSClient;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import common.enums.eKVClusterOperationType;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

public class KVCommandModifyClusterNode extends KVCommand<ECSClient> {
    private static final String CLUSTER_CLUSTER_NAME_KEY = "cluster_name";
    private static final String CLUSTER_NODE_NAME_KEY = "node_name";
    public static final String CLUSTER_OPERATION = "'cluster_operation'";

    public KVCommandModifyClusterNode(KVCommandPattern.KVCommandType commandType) {
        super(commandType);
    }


    @Override
    public KVJSONMessage execute(ECSClient Instance) {
        KVJSONMessage ret = new KVJSONMessage();
        ret.setExtendStatus(eKVExtendStatusType.REPLICA_ERROR);
        switch (this.getClusterOperationType()){
            case JOIN:{
                if(Instance.joinCluster(getClusterName(),getNodeName())){
                    ret.setExtendStatus(eKVExtendStatusType.REPLICA_OK);
                }
            }
            case EXIT:{
                if(Instance.leaveCluster(getClusterName(),getNodeName())){
                    ret.setExtendStatus(eKVExtendStatusType.REPLICA_OK);
                }
            }
        }
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if(response.getExtendStatusType() == eKVExtendStatusType.REPLICA_OK){
            kv_out.println_info(String.format("Operation on cluster %s for node %s was success\n",getClusterName(),getNodeName()));
        }
        else{
            kv_out.println_info(String.format("Operation on cluster %s for node %s was failed\n",getClusterName(),getNodeName()));
        }
    }

    public void setClusterName(String name){
        set(CLUSTER_CLUSTER_NAME_KEY,name);
    }
    public String getClusterName(){
        return getValue(CLUSTER_CLUSTER_NAME_KEY);
    }
    public void setNodeName(String nodeName){
        set(CLUSTER_NODE_NAME_KEY,nodeName);
    }
    public String getNodeName(){
        return getValue(CLUSTER_NODE_NAME_KEY);
    }
    public void setClusterOperation(eKVClusterOperationType operationType){
        set(CLUSTER_OPERATION,Integer.toString(operationType.toInt()));
    }
    public eKVClusterOperationType getClusterOperationType(){
        return eKVClusterOperationType.fromInt(Integer.parseInt(getValue(CLUSTER_OPERATION)));
    }
}
