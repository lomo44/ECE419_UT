package common.metadata;

import common.KVNetworkID;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

public class KVMetadataController {
    KVMetadata metaData;
    MessageDigest digestObject;
    SortedSet<BigInteger> keys;
    public static final String METADATA_ENCODING = "UTF-8";
    public KVMetadataController() throws NoSuchAlgorithmException {
        digestObject = initializeDigest();
    }
    public boolean update(KVMetadata newData){
        boolean changed;
        if(metaData == null){
            metaData = newData;
            changed = true;
        }
        else {
            changed = metaData.merge(newData);
        }
        if (changed) {
            // Rebuild the sorted key set
            this.keys = new TreeSet<>(this.metaData.getKeys());
        }
        return changed;
    }
    public KVNetworkID getNetowrkIDMap(BigInteger hash){
        for(BigInteger key : keys){
            if(key.compareTo(hash)>=1){
                return metaData.getNetworkIDFromHash(key);
            }
        }
        return metaData.getNetworkIDFromHash(keys.first());
    }

    public MessageDigest initializeDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5");
    }

    public void addNetworkID(KVNetworkID id) throws Exception {
        String idString = id.toString();
        BigInteger hash = hash(idString);
        if(this.metaData==null){
            update(new KVMetadata());
        }
        if(!metaData.hasNetworkIDHash(hash)){
            this.metaData.addNetworkIDHashPair(hash,id);
            this.keys.add(hash);
        }
    }

    public BigInteger hash(String input) throws Exception{
        byte[] bytesOfMessage = input.getBytes(METADATA_ENCODING);
        byte[] thedigest = digestObject.digest(bytesOfMessage);
        return new BigInteger(1,thedigest);
    }
}
