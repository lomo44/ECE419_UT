package common.metadata;

import common.datastructure.KVRange;
import common.networknode.*;
import database.storage.KVStorage;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
            this.keys = new TreeSet<>(this.metaData.getStorageNodeHashes());
            generateHashRange();
        }
        return changed;
    }
    
    public KVMetadata getMetaData() {
    		return metaData;
    }
    /**
     * Given a hash, return its responsible NetworkNode
     * @param hash input hash
     * @return KVStorageNode if there is a responsible node, null if not.
     */
    public KVStorageNode getResponsibleStorageNode(BigInteger hash){
        for(BigInteger key: keys){
            KVStorageNode node = metaData.getStorageNodeFromHash(key);
            if(node.getHashRange().inRange(hash)){
                return node;
            }
        }
        return null;
    }

    /**
     * Get keys
     * @return sorted set of hashed keys
     */
    public SortedSet<BigInteger> getHashes() {
        return this.keys;
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
     * Get storage node from hash
     * @return KVStorageNode object
     */
    public KVStorageNode getStorageNodeFromHash(BigInteger hash) {
        return metaData.getStorageNodeFromHash(hash);
    }
    

    /**
     * Add a new NetworkNode into the location
     * @param node new KVNetworkNode;
     */
    public void addStorageNode(KVStorageNode node) {
        String idString = node.toString();
        BigInteger hash = hash(idString);
        if(this.metaData==null){
            update(new KVMetadata());
        }
        if(!metaData.hasStorageNodeByHash(hash)){
            this.metaData.addStorageNodeHashPair(hash,node);
            this.keys.add(hash);
            generateHashRange();
        }
    }
    
    public void createMetaData(List<KVStorageNode> nodes) {
    		Iterator<KVStorageNode> itor = nodes.iterator();
    			metaData = new KVMetadata();
    			this.keys = new TreeSet<>();
    		while(itor.hasNext()) {
    			KVStorageNode node=itor.next();
    			String idString = node.toString();
    	        BigInteger hash = hash(idString);
    			if(!metaData.hasStorageNodeByHash(hash)){
    	            this.metaData.addStorageNodeHashPair(hash,node);
    	            this.keys.add(hash);    		
    			}
    		}
		generateHashRange();
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

    private void generateHashRange(){
        Iterator<BigInteger> itor = keys.iterator();
        if(itor.hasNext()){
            BigInteger previous = itor.next();
            while(itor.hasNext()){
                BigInteger current = itor.next();
                metaData.getStorageNodeFromHash(previous).setHashRange(new KVRange<>(current,previous.subtract(BigInteger.valueOf(1)),true,true));
                previous = current;
            }
            metaData.getStorageNodeFromHash(previous).setHashRange(new KVRange<>(keys.first(),previous.subtract(BigInteger.valueOf(1)),true,true));
        }
    }

    public KVStorageNode getStorageNode(String hostname, int portNumber){
        return getStorageNode(new KVNetworkNode(hostname,portNumber));
    }

    public KVStorageNode getStorageNode(String targetName){
        return getStorageNode(KVNetworkNode.fromString(targetName));
    }

    public KVStorageNode getStorageNode(KVNetworkNode node){
        return getCurrentMetaData().getStorageNodeFromHash(hash(node.toString()));
    }


    public KVMetadata getCurrentMetaData(){
        return metaData;
    }
}
