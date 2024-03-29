package common.metadata;

import common.messages.KVExclusiveMessage;
import common.messages.KVJSONMessage;
import common.networknode.KVStorageCluster;
import common.networknode.KVStorageNode;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.*;

public class KVMetadata {
    private HashMap<BigInteger, KVStorageNode> storageNodes;
    private static final String KVMETADATA_IDENTIFIER = "19a67338-3e44-4a07-a94c-dcde45767519";
    private static final String KVMETADATA_PAYLOAD_ID = "dc67a029-52e1-465d-9128-7ec8d8e660f9";

    /**
     * Create new KVMetadata object
     */
    public KVMetadata(){
        storageNodes = new HashMap<>();
    }

    public static KVMetadata fromBytes(byte[] data){
        KVJSONMessage msg = new KVJSONMessage();
        msg.fromBytes(data,0,data.length);
        return fromKVJSONMessage(msg);
    }
    /**
     * Create a KVMetadata from a KVJSONMessage
     * @param msg KVJSONMessage
     * @return KVMetadata is conversion success, null if conversion failed.
     */
    public static KVMetadata fromKVJSONMessage(KVJSONMessage msg) {
        KVMetadata data = null;
        KVExclusiveMessage msgParsed = new KVExclusiveMessage(KVMETADATA_IDENTIFIER,KVMETADATA_PAYLOAD_ID);
        if(msgParsed.loadFromKVJSONMessage(msg)){
            data = new KVMetadata();
            for (String hash: msgParsed.keySet()
                 ) {
                JSONObject object = new JSONObject(msgParsed.get(hash));
                KVStorageNode node = KVStorageNode.fromJSONObject(object);
                switch (node.getNodeType()){
                    case STORAGE_CLUSTER:{
                        node = KVStorageCluster.fromJSONObject(object);
                        break;
                    }
                }
                if(node!=null){
                    data.addStorageNodeHashPair(new BigInteger(hash),node);
                }
                else{
                    return null;
                }
            }
        }
        return data;
    }

    public Collection<KVStorageNode> getStorageNodes(){
        return storageNodes.values();
    }

    /**
     * Convert metadata to KVJSONMessage
     * @return KVJSONMessage instance
     */
    public KVJSONMessage toKVJSONMessage(){
        KVExclusiveMessage msg = new KVExclusiveMessage(KVMETADATA_IDENTIFIER,KVMETADATA_PAYLOAD_ID);
        for(BigInteger key : storageNodes.keySet()){
            msg.put(key.toString(), storageNodes.get(key).toJSONObject().toString());
        }
        return msg.toKVJSONMessage();
    }

    public byte[] toBytes(){
        return toKVJSONMessage().toBytes();
    }


    public void addAll(KVMetadata data){
        this.storageNodes.putAll(data.getStorageNodeMap());
    }

    /**
     * Add a new KVNetworkNode into the metadata
     * @param hash Hash for the KVNetworkNode
     * @param id KVNetworkNode instance
     */
    public void addStorageNodeHashPair(BigInteger hash, KVStorageNode id){
        storageNodes.put(hash,id);
    }


    public void removeStorageNodeHashPair(BigInteger hash){
        storageNodes.remove(hash);
    }
    /**
     * Fetch network ID based on a hash value
     * @param hash hash value
     * @return KVNetworkNode instance if found, null if not found
     */
    public KVStorageNode getStorageNodeFromHash(BigInteger hash){
        return storageNodes.get(hash);
    }

    /**
     * Check if the hash corresponds to a KVNetworkNode
     * @param hash Hash value
     * @return true if a KVNetworkNode corresponds to this hash, false if not.
     */
    boolean hasStorageNodeByHash(BigInteger hash){
        return storageNodes.containsKey(hash);
    }

    /**
     * Get all of the network id hashes
     * @return
     */
    public Set<BigInteger> getStorageNodeHashes(){
        return storageNodes.keySet();
    }

    /**
     * Return KVNetworkNode mapping
     * @return KVNetworkNode hash
     */
    public HashMap<BigInteger, KVStorageNode> getStorageNodeMap(){
        return storageNodes;
    }

    /**
     * Merge a KVMetadata into current one
     * @param data new KVMetadata
     * @return true if current one is different after merged. false if not.
     */
    public boolean merge(KVMetadata data){
        boolean isequal = this.storageNodes.equals(data.getStorageNodeMap());
        if(!isequal){
            this.storageNodes.putAll(data.getStorageNodeMap());
        }
        return !isequal;
    }

    /**
     * Check if the incoming object is equals to the current one.
     * @param o incoming object
     * @return true if incoming object is as same as the current one
     */
    @Override
    public boolean equals(Object o) {
        KVMetadata in = (KVMetadata) o;
        boolean ret = this.storageNodes.equals(in.getStorageNodeMap());
        return ret;
    }

    /**
     * Clear the content of the metadata
     */
    public void clear(){
        this.storageNodes.clear();
    }

    public void print(){
        for (BigInteger hash: storageNodes.keySet()
             ) {
            System.out.println(String.format("Hash: %s, Node: %s",
                    hash,storageNodes.get(hash).toJSONObject().toString()));
        }
    }

    public List<KVStorageNode> getIrrelevantNodes(String nodeUID){
        Collection<KVStorageNode> allNodes = getStorageNodes();
        List<KVStorageNode> ret = new ArrayList<>();
        for(KVStorageNode node : allNodes){
            switch (node.getNodeType()){
                case STORAGE_NODE:{
                    if(!node.getUID().matches(nodeUID)){
                        ret.add(node);
                    }
                    break;
                }
                case STORAGE_CLUSTER:{
                    KVStorageCluster cluster = (KVStorageCluster) node;
                    if(!cluster.contain(nodeUID)){
                        ret.add(node);
                    }
                    break;
                }
            }
        }
        return ret;
    }
    public Collection<KVStorageNode> getReleventNodes(String nodeUID){
        Collection<KVStorageNode> allNodes = getStorageNodes();
        List<KVStorageNode> ret = new ArrayList<>();
        for(KVStorageNode node : allNodes){
            switch (node.getNodeType()){
                case STORAGE_NODE:{
                    if(node.getUID().matches(nodeUID)){
                        ret.add(node);
                    }
                    break;
                }
                case STORAGE_CLUSTER:{
                    KVStorageCluster cluster = (KVStorageCluster) node;
                    if(cluster.contain(nodeUID)){
                        ret.add(cluster);
                    }
                    break;
                }
            }
        }
        return ret;
    }
}
