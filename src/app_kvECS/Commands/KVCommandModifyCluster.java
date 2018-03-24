package app_kvECS.Commands;

import app_kvECS.ECSClient;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import common.enums.eKVClusterOperationType;
import common.enums.eKVClusterStatus;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

public class KVCommandModifyCluster extends KVCommand<ECSClient>{
    public static final String CLUSTER_NAME_KEY = "cluster_name";
    public static final String CLUSTER_OPEARION_TYPE_KEY = "cluster_operation";
    public KVCommandModifyCluster(KVCommandPattern.KVCommandType commandType) {
        super(commandType);
    }

    @Override
    public KVJSONMessage execute(ECSClient Instance) {
        KVJSONMessage ret = new KVJSONMessage();
        ret.setExtendStatus(eKVExtendStatusType.REPLICA_OK);
        switch (getClusterOperationType()){
            case CREATE:{
                if(Instance.createCluster(getClusterName())== eKVClusterStatus.INVALID){
                    ret.setExtendStatus(eKVExtendStatusType.REPLICA_FAIL);
                }
            }
            case REMOVE:{
                if(Instance.removeCluster(getClusterName())==eKVClusterStatus.EXIST){
                    ret.setExtendStatus(eKVExtendStatusType.REPLICA_FAIL);
                }
            }
        }
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if(response.getExtendStatusType()==eKVExtendStatusType.REPLICA_OK){
            kv_out.println_info(String.format("Cluster %s operation success\n",getClusterName()));
        }
        else{
            kv_out.println_info(String.format("Cluster %s operation fail\n",getClusterName()));
        }
    }

    public void setClusterName(String clusterName){
        set(CLUSTER_NAME_KEY,clusterName);
    }

    public String getClusterName(){
        return getValue(CLUSTER_NAME_KEY);
    }

    public void setClusterOperationType(eKVClusterOperationType type){
        set(CLUSTER_OPEARION_TYPE_KEY,Integer.toString(type.toInt()));
    }

    public eKVClusterOperationType getClusterOperationType(){
        return eKVClusterOperationType.fromInt(Integer.parseInt(getValue(CLUSTER_OPEARION_TYPE_KEY)));
    }
}
