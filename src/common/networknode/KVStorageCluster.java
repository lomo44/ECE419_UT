package common.networknode;

import app_kvServer.KVServerConfig;
import common.enums.eKVNetworkNodeType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class KVStorageCluster extends KVStorageNode {
    private static final String JSON_KEY_NODELIST = "node_list";
    private static final String JSON_KEY_PRIMARY = "primary_node";
    private HashMap<String, KVStorageNode> childNodes = new HashMap<>();
    private String primaryNodeUID = "";

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
        this.primaryNodeUID = primary.getServerName();
        for(KVServerConfig member_config: members){
            addNode(new KVStorageNode(member_config));
        }
    }
    public static KVStorageCluster fromJSONObject(JSONObject obj){
        KVStorageCluster cluster =  new KVStorageCluster(KVStorageNode.fromJSONObject(obj));
        JSONArray listObject = obj.getJSONArray(JSON_KEY_NODELIST);
        for(int i = 0; i < listObject.length(); i++){
            JSONObject object = listObject.getJSONObject(i);
            KVNetworkNode node = KVNetworkNode.fromJSONObject(object);
            switch (node.getNodeType()){
                case STORAGE_NODE:{
                    cluster.addNode(KVStorageNode.fromJSONObject(object));
                    break;
                }
                case STORAGE_CLUSTER:{
                    KVStorageCluster subCluster =  KVStorageCluster.fromJSONObject(object);
                    cluster.addNode(subCluster);
                    break;
                }
            }
        }
        cluster.setPrimaryNodeUID(obj.getString(JSON_KEY_PRIMARY));
        return cluster;
    }
    public Collection<KVStorageNode> getChildNodes() {
        return childNodes.values();
    }
    public Collection<KVStorageNode> getChildNodesWithoutPrimary(){
        List<KVStorageNode> ret = new ArrayList<>();
        Collection<KVStorageNode> nodes = getChildNodes();
        for(KVStorageNode node : nodes){
            if(!isPrimary(node)){
                ret.add(node);
            }
        }
        return ret;
    }
    public void addNode(KVStorageNode node){
        this.childNodes.put(node.UID,node);
    }
    public void removeNodeByUID(String UID){
        if(UID.matches(this.primaryNodeUID)){
            primaryNodeUID = "";
        }
        childNodes.remove(UID);
    }
    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();
        JSONArray listObject = new JSONArray();
        for (String nodeName: childNodes.keySet()
                ) {
            listObject.put(childNodes.get(nodeName).toJSONObject());
        }
        jsonObject.put(JSON_KEY_NODELIST,listObject);
        jsonObject.put(JSON_KEY_PRIMARY, primaryNodeUID);
        return jsonObject;
    }
    @Override
    public Socket createSocket() throws IOException {
        // should not create socket based on cluster either.
        return null;
    }
    public void setPrimaryNodeUID(String UID) {
        this.primaryNodeUID = UID;
    }
    public KVStorageNode getPrimaryNode() {
        return childNodes.get(this.primaryNodeUID);
    }
    public String getPrimaryNodeUID(){
        return primaryNodeUID;
    }
    public KVStorageNode getRandomMember(){
        System.out.printf("Current childnode size: %d\n",childNodes.size());
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
    public boolean isPrimary(KVStorageNode node){
        return this.primaryNodeUID.matches(node.getUID());
    }
    public boolean isPrimary(String UID){
        return this.primaryNodeUID.matches(UID);
    }
    public boolean contain(String UID){
        return childNodes.containsKey(UID);
    }
    public int getNumOfMembers(){
        return childNodes.size();
    }
    public boolean isEmpty(){
        return getChildNodes().size()==0;
    }
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
