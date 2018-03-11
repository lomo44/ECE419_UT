package common.metadata;

import common.messages.KVExclusiveMessage;
import common.messages.KVJSONMessage;
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
            HashMap<String, String> entries = msgParsed.getEntries();
            Set<String> hashes = msgParsed.getEntries().keySet();
            for (String hash: hashes
                 ) {
                String networkIDstring = entries.get(hash);
                KVStorageNode newID = KVStorageNode.fromString(networkIDstring);
                if(newID!=null){
                    data.addStorageNodeHashPair(new BigInteger(hash),newID);
                }
                else{
                    return null;
                }
            }
        }
        return data;
    }

    public List<KVStorageNode> getStorageNodes(){
        List<KVStorageNode> ret = new ArrayList<>();
        ret.addAll(storageNodes.values());
        return ret;
    }

    /**
     * Convert metadata to KVJSONMessage
     * @return KVJSONMessage instance
     */
    public KVJSONMessage toKVJSONMessage(){
        KVExclusiveMessage msg = new KVExclusiveMessage(KVMETADATA_IDENTIFIER,KVMETADATA_PAYLOAD_ID);
        for(BigInteger key : storageNodes.keySet()){
            msg.add(key.toString(), storageNodes.get(key).toString());
        }
        return msg.toKVJSONMessage();
    }

    public byte[] toBytes(){
        return toKVJSONMessage().toBytes();
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
}
