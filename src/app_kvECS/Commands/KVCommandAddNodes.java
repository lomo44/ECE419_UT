package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;
import ecs.IECSNode;

import java.util.Collection;

public class KVCommandAddNodes extends KVCommand<ECSClient> {
    public static final String NODE_COUNT_KEY = "num_nodes";
    public static final String CACHE_SIZE_KEY = "cache_size";
    public static final String CACHE_STRAT_KEY = "cache_strategy";
    public static final String CLUSTER_NAME_KEY = "cluster_name";
    public KVCommandAddNodes() { super(KVCommandPattern.KVCommandType.ADD_NODES); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        KVJSONMessage ret = new KVJSONMessage();
        Collection<IECSNode> nodes = clientInstance.addNodes(getNumNodes(),getCacheStrategy(),getCacheSize(),getClusterName());
        if(nodes ==null || nodes.size()!=getNumNodes()){
            ret.setExtendStatus(eKVExtendStatusType.ADD_NODE_FAIL);
        }
        else{
            ret.setExtendStatus(eKVExtendStatusType.ADD_NODE_SUCCESS);
        }
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

    public void setNumNodes(int numNodes) {
        set(NODE_COUNT_KEY,Integer.toString(numNodes));
    }
    public void setCacheSize(int cacheSize) {
        set(CACHE_SIZE_KEY,Integer.toString(cacheSize));
    }
    public void setCacheStrategy(String cacheStrategy) {
        set(CACHE_STRAT_KEY,cacheStrategy);
    }
    public void setClusterName(String clusterName){set(CLUSTER_NAME_KEY,clusterName);}
    public int getNumNodes() {
        return Integer.parseInt(getValue(NODE_COUNT_KEY));
    }
    public int getCacheSize() {
        return Integer.parseInt(getValue(CACHE_SIZE_KEY));
    }
    public String getCacheStrategy() {
        return getValue(CACHE_STRAT_KEY);
    }
    public String getClusterName(){return getValue(CLUSTER_NAME_KEY);}
}
