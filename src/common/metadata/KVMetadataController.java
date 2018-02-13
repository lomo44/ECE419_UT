package common.metadata;

import common.KVNetworkNode;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedSet;
import java.util.TreeSet;

public class KVMetadataController {
    KVMetadata metaData;
    MessageDigest digestObject;
    SortedSet<BigInteger> keys;

    public static final String METADATA_ENCODING = "UTF-8";

    /**
     * Create a KVMetadataController instance with no metadata.
     *      */
    public KVMetadataController(){
        digestObject = initializeDigest();
    }

    /**
     * Update current metadata with new Data.This method will merge entries in the newdata into the current one
     * Also update the sorted key set for KVNetworkNode hashing
     * @param newData new KVMetadata
     * @return True if there are any changes compared to the previous metadata, False if not
     */
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
            this.keys = new TreeSet<>(this.metaData.getNetworkIDHashes());
        }
        return changed;
    }

    /**
     * Given a hash, return its mapped NetworkID
     * @param hash input hash
     * @return KVNetworkNode
     */
    public KVNetworkNode getNetowrkIDMap(BigInteger hash){
        for(BigInteger key : keys){
            if(key.compareTo(hash)>=1){
                return metaData.getNetworkIDFromHash(key);
            }
        }
        return metaData.getNetworkIDFromHash(keys.first());
    }

    /**
     * Initialize digest algorithm
     * @return MessageDigest object
     */
    public MessageDigest initializeDigest(){
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add a new NetworkID into the location
     * @param id new KVNetworkNode;
     */
    public void addNetworkID(KVNetworkNode id) {
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

    /**
     * Hash an input string
     * @param input input string
     * @return Hash value
     */
    public BigInteger hash(String input) {
        byte[] bytesOfMessage = new byte[0];
        try {
            bytesOfMessage = input.getBytes(METADATA_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] thedigest = digestObject.digest(bytesOfMessage);
        return new BigInteger(1,thedigest);
    }
}
