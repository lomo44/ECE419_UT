package common.messages;

import common.enums.eKVClusterOperationType;

public class KVClusterOperationMessage extends KVExclusiveMessage{
    private final static String KVCLUSTEROPERATIONMESSAGE_IDENTIFIER = "8643e4e0-2d90-11e8-9968-4f749db82aa9";
    private final static String KVCLUSTEROPERATIONMESSAGE_PAYLOADID = "8643e4e0-2d90-11e8-9968-4f749db82aa9";
    private final static String KVCLUSTEROPERATIONMESSAGE_TARGETCLUSTER = "target_cluster";
    private final static String KVCLUSTEROPERATIONMESSAGE_OPERATION = "operation";
    public KVClusterOperationMessage() {
        super(KVCLUSTEROPERATIONMESSAGE_IDENTIFIER, KVCLUSTEROPERATIONMESSAGE_PAYLOADID);
    }
    public void setTargetCluster(String clusterUID){
        this.add(KVCLUSTEROPERATIONMESSAGE_TARGETCLUSTER,clusterUID);
    }
    public String getTargetCluster(){
        return get(KVCLUSTEROPERATIONMESSAGE_TARGETCLUSTER);
    }

    public void setOperationType(eKVClusterOperationType operation){
        this.add(KVCLUSTEROPERATIONMESSAGE_OPERATION,Integer.toString(operation.toInt()));
    }

    public eKVClusterOperationType getOperationType(){
        return eKVClusterOperationType.fromInt(Integer.parseInt(this.get(KVCLUSTEROPERATIONMESSAGE_OPERATION)));
    }

    public static KVClusterOperationMessage fromKVJSONMessage(KVJSONMessage msg){
        KVClusterOperationMessage ret = new KVClusterOperationMessage();
        if(ret.loadFromKVJSONMessage(msg)){
            return ret;
        }
        else{
            return null;
        }
    }
}
