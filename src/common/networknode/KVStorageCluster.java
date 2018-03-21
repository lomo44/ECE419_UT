package common.networknode;

import app_kvServer.KVServerConfig;
import common.enums.eKVNetworkNodeType;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KVStorageCluster extends KVStorageNode {
    private static final String JSON_KEY_NODELIST = "node_list";
    private static final String JSON_KEY_PRIMARY = "primary_node";
    private HashMap<String, KVStorageNode> childNodes = new HashMap<>();
    private String primaryNode = "";

    public KVStorageCluster(String UID) {
        super("KVStorageCluster", -1, UID);
        this.nodeType = eKVNetworkNodeType.STORAGE_CLUSTER;
    }
    public KVStorageCluster(String hostname, int portNumber, String servername) {
        this(servername);
    }
    public KVStorageCluster(KVStorageNode node) {
        this(node.getUID());
        this.setHashRange(node.getHashRange());
    }
    public KVStorageCluster(String UID, KVServerConfig primary, List<KVServerConfig> members){
        this(UID);
        this.primaryNode = primary.getServerName();
        for(KVServerConfig member_config: members){
            addNode(new KVStorageNode(member_config));
        }
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
                    cluster.addNode(subCluster);
                    break;
                }
            }
        }
        cluster.setPrimaryNode(obj.getString(JSON_KEY_PRIMARY));
        return cluster;
    }
    public Collection<KVStorageNode> getChildNodes() {
        return childNodes.values();
    }
    public void addNode(KVStorageNode node){
        this.childNodes.put(node.UID,node);
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
        jsonObject.put(JSON_KEY_PRIMARY,primaryNode);
        return jsonObject;
    }
    @Override
    public Socket createSocket() throws IOException {
        // should not create socket based on cluster either.
        return null;
    }
    public void setPrimaryNode(String UID) {
        this.primaryNode = UID;
    }
    public KVStorageNode getPrimaryNode() {
        return childNodes.get(this.primaryNode);
    }
    public KVStorageNode getRandomMember(){
        int index = ThreadLocalRandom.current().nextInt(0,childNodes.size());
        int counter = 0;
        for(KVStorageNode node: childNodes.values()){
            if(counter==index){
                return node;
            }
            counter++;
        }
        return null;
    }
}
