package common.metadata;

import common.messages.KVJSONMessage;
import ecs.ECSNode;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.*;

public class KVMetadata {
    private HashMap<BigInteger, ECSNode> storageNodes;
    private static final String KVMETADATA_TAG = "19a67338-3e44-4a07-a94c-dcde45767519";
    private static final String KVMETADATA_STORAGE_NODE = "dc67a029-52e1-465d-9128-7ec8d8e660f9";

    /**
     * Create new KVMetadata object
     */
    public KVMetadata(){
        storageNodes = new HashMap<>();
    }

    /**
     * Create a KVMetadata from a KVJSONMessage
     * @param msg KVJSONMessage
     * @return KVMetadata is conversion success, null if conversion failed.
     */
    public static KVMetadata fromKVJSONMessage(KVJSONMessage msg) {
        KVMetadata data;
        if(msg.getKey().matches(KVMETADATA_TAG)){
            data = new KVMetadata();
            // Proper metadata, extract the value file;
            String value = msg.getValue();
            JSONObject object = new JSONObject(value);
            if(object.has(KVMETADATA_STORAGE_NODE)){
                JSONObject map = object.getJSONObject(KVMETADATA_STORAGE_NODE);
                Iterator<String> itor = map.keys();
                while(itor.hasNext()){
                    String hash = itor.next();
                    String networkIDstring = map.getString(hash);
                    ECSNode newID = ECSNode.fromString(networkIDstring);
                    if(newID!=null){
                        data.addStorageNodeHashPair(new BigInteger(hash),newID);
                    }
                    else{
                        return null;
                    }
                }
                return data;
            }
            return null;
        }
        return null;
    }

    /**
     * Convert metadata to KVJSONMessage
     * @return KVJSONMessage instance
     */
    public KVJSONMessage toKVJSONMessage(){
        KVJSONMessage msg = new KVJSONMessage() ;
        JSONObject object = new JSONObject();
        // Create map
        Map<String,String> outputmap = new HashMap<>();
        for(BigInteger key : storageNodes.keySet()){
            outputmap.put(key.toString(), storageNodes.get(key).toString());
        }
        object.put(KVMETADATA_STORAGE_NODE,outputmap);
        msg.setValue(object.toString());
        msg.setKey(KVMETADATA_TAG);
        return msg;
    }

    /**
     * Add a new KVNetworkNode into the metadata
     * @param hash Hash for the KVNetworkNode
     * @param id KVNetworkNode instance
     */
    public void addStorageNodeHashPair(BigInteger hash, ECSNode id){
        storageNodes.put(hash,id);
    }

    /**
     * Fetch network ID based on a hash value
     * @param hash hash value
     * @return KVNetworkNode instance if found, null if not found
     */
    public ECSNode getStorageNodeFromHash(BigInteger hash){
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
    private HashMap<BigInteger, ECSNode> getStorageNodeMap(){
        return storageNodes;
    }

    /**
     * Merge a KVMetadata into current one
     * @param data new KVMetadata
     * @return true if current one is different after merged. false if not.
     */
    public boolean merge(KVMetadata data){
        boolean isequal = this.storageNodes.equals(data.getStorageNodeMap());
        this.storageNodes.putAll(data.getStorageNodeMap());
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
}
