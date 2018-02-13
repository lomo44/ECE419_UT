package common.metadata;

import common.KVNetworkID;
import common.messages.KVJSONMessage;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.*;

public class KVMetadata {
    private HashMap<BigInteger,KVNetworkID> data;
    private static final String KVMETADATA_TAG = "19a67338-3e44-4a07-a94c-dcde45767519";
    private static final String KVMETADATA_VALUE = "dc67a029-52e1-465d-9128-7ec8d8e660f9";

    /**
     * Create new KVMetadata object
     */
    public KVMetadata(){
        data = new HashMap<>();
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
            if(object.has(KVMETADATA_VALUE)){
                JSONObject map = object.getJSONObject(KVMETADATA_VALUE);
                Iterator<String> itor = map.keys();
                while(itor.hasNext()){
                    String hash = itor.next();
                    String networkIDstring = map.getString(hash);
                    KVNetworkID newID = KVNetworkID.fromString(networkIDstring);
                    if(newID!=null){
                        data.addNetworkIDHashPair(new BigInteger(hash),newID);
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
        for(BigInteger key : data.keySet()){
            outputmap.put(key.toString(),data.get(key).toString());
        }
        object.put(KVMETADATA_VALUE,outputmap);
        msg.setValue(object.toString());
        msg.setKey(KVMETADATA_TAG);
        return msg;
    }

    /**
     * Add a new KVNetworkID into the metadata
     * @param hash Hash for the KVNetworkID
     * @param id KVNetworkID instance
     */
    public void addNetworkIDHashPair(BigInteger hash, KVNetworkID id){
        data.put(hash,id);
    }

    /**
     * Fetch network ID based on a hash value
     * @param hash hash value
     * @return KVNetworkID instance if found, null if not found
     */
    public KVNetworkID getNetworkIDFromHash(BigInteger hash){
        return data.get(hash);
    }

    /**
     * Check if the hash corresponds to a KVNetworkID
     * @param hash Hash value
     * @return true if a KVNetworkID corresponds to this hash, false if not.
     */
    boolean hasNetworkIDHash(BigInteger hash){
        return data.containsKey(hash);
    }

    /**
     * Get all of the network id hashes
     * @return
     */
    public Set<BigInteger> getNetworkIDHashes(){
        return data.keySet();
    }

    /**
     * Return KVNetworkID mapping
     * @return KVNetworkID hash
     */
    private HashMap<BigInteger,KVNetworkID> getKVNetworkIDMap(){
        return data;
    }

    /**
     * Merge a KVMetadata into current one
     * @param data new KVMetadata
     * @return true if current one is different after merged. false if not.
     */
    public boolean merge(KVMetadata data){
        boolean isequal = this.data.equals(data.getKVNetworkIDMap());
        this.data.putAll(data.getKVNetworkIDMap());
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
        boolean ret = this.data.equals(in.getKVNetworkIDMap());
        return ret;
    }
}
