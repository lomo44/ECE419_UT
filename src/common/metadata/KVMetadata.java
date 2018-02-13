package common.metadata;

import common.KVNetworkID;
import common.messages.KVJSONMessage;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class KVMetadata {
    private HashMap<BigInteger,KVNetworkID> data;
    private static final String KVMETADATA_TAG = "META_DATA";
    private static final String KVMETADATA_VALUE = "META_DATA_VALUE";
    public KVMetadata(){
        data = new HashMap<>();
    }
    public static KVMetadata fromKVJSONMessage(KVJSONMessage msg) {
        KVMetadata data = new KVMetadata();
        if(msg.getKey().matches(KVMETADATA_TAG)){
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
                        break;
                    }
                }
            }
        }
        return data;
    }
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
    public void addNetworkIDHashPair(BigInteger hash, KVNetworkID id){
        data.put(hash,id);
    }
    public KVNetworkID getNetworkIDFromHash(BigInteger hash){
        return data.get(hash);
    }
    boolean hasNetworkIDHash(BigInteger hash){
        return data.containsKey(hash);
    }
    public Set<BigInteger> getKeys(){
        return data.keySet();
    }
    private HashMap<BigInteger,KVNetworkID> getData(){
        return data;
    }
    public boolean merge(KVMetadata data){
        boolean isequal = this.data.equals(data.getData());
        this.data.putAll(data.getData());
        return !isequal;
    }
    @Override
    public boolean equals(Object o) {
        KVMetadata in = (KVMetadata) o;
        boolean ret = this.data.equals(in.getData());
        return ret;
    }
}
