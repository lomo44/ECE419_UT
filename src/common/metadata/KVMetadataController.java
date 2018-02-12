package common.metadata;

import common.KVNetworkID;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

public class KVMetadataController {
    KVMetadata metaData;
    MessageDigest digestObject;
    SortedSet<String> keys;
    public KVMetadataController() throws NoSuchAlgorithmException {
        digestObject = initializeDigest();
    }
    public boolean update(KVMetadata newData){
        boolean changed = metaData.merge(newData);
        if(changed){
            this.keys = new TreeSet<String>(this.metaData.getKeys());
        }
        return changed;
    }
    public KVNetworkID getNetowrkIDMap(String hash){
        for(String key : keys){
            if(key.compareTo(hash)>=1){
                return metaData.getNetworkIDFromHash(key);
            }
        }
        return metaData.getNetworkIDFromHash(keys.first());
    }
    public KVMetadata getCurrentMetaData(){
        return metaData;
    }
    public MessageDigest initializeDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5");
    }

    public void addNetworkID(KVNetworkID id) throws Exception {
        String idString = id.toString();
        String hash = hash(idString);
        if(this.metaData==null){
            this.metaData = new KVMetadata();
        }
        this.metaData.addNetworkIDHashPair(hash,id);
    }

    public String hash(String input) throws Exception{
        byte[] bytesOfMessage = input.getBytes("UTF-8");
        byte[] thedigest = digestObject.digest(bytesOfMessage);
        return Arrays.toString(thedigest);
    }
}
