package common.networknode;

import common.communication.KVCommunicationModuleSet;
import common.enums.eKVNetworkNodeType;
import database.storage.KVStorage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class KVStorageCluster extends KVStorageNode {
    private static final String JSON_KEY_NODELIST = "node_list";
    private HashMap<String, KVStorageNode> childNodes = new HashMap<>();
    private KVStorageCluster parentNodes;
    private KVCommunicationModuleSet childNodesCommunicationSet = null;

    public KVStorageCluster(String UID) {
        super("undefined", -1, UID);
        this.nodeType = eKVNetworkNodeType.STORAGE_CLUSTER;
    }
    public KVStorageCluster(String hostname, int portNumber, String servername) {
        this(servername);
    }

    public KVStorageCluster(KVStorageNode node) {
        this(node.getUID());
        this.setHashRange(node.getHashRange());
    }
    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();
        JSONObject listObject = new JSONObject();
        for (String nodeName: childNodes.keySet()
             ) {
            listObject.put(
                    Integer.toString(childNodes.get(nodeName).getNodeType().toInt()),
                    childNodes.get(nodeName).toJSONObject());
        }
        jsonObject.put(JSON_KEY_NODELIST,listObject);
        return jsonObject;
    }

    public static KVStorageCluster fromJSONObject(JSONObject obj){
        KVStorageCluster cluster =  new KVStorageCluster(KVStorageNode.fromJSONObject(obj));
        JSONObject listObject = obj.getJSONObject(JSON_KEY_NODELIST);
        for(String key:listObject.keySet()){
            eKVNetworkNodeType type = eKVNetworkNodeType.fromInt(Integer.parseInt(key));
            switch (type){
                case STORAGE_NODE:{
                    cluster.addNode(KVStorageNode.fromJSONObject(listObject.getJSONObject(key)));
                    break;
                }
                case STORAGE_CLUSTER:{
                    KVStorageCluster subCluster =  KVStorageCluster.fromJSONObject(listObject.getJSONObject(key));
                    subCluster.setParentNodes(cluster);
                    cluster.addNode(subCluster);
                    break;
                }
            }
        }
        return cluster;
    }

    @Override
    public Socket createSocket() throws IOException {
        // should not create socket based on cluster either.
        return null;
    }

    public KVStorageCluster getParentNodes(){
        return this.parentNodes;
    }

    public Collection<KVStorageNode> getChildNodes() {
        return childNodes.values();
    }

    public void setParentNodes(KVStorageCluster parentNodes) {
        this.parentNodes = parentNodes;
    }

    public void addNode(KVStorageNode node){
        this.childNodes.put(node.UID,node);
    }

}
