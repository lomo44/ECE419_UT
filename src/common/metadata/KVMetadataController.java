package common.metadata;

import common.datastructure.KVRange;
import common.enums.eKVNetworkNodeType;
import common.networknode.KVStorageCluster;
import common.networknode.KVStorageNode;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
    public void update(KVMetadata newData){
        if(metaData == null){
            metaData = new KVMetadata();
        }
        else {
            metaData.clear();
            metaData.addAll(newData);
        }
        this.keys = new TreeSet<>(this.metaData.getStorageNodeHashes());
        generateHashRange();
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
        for(BigInteger key: metaData.getStorageNodeHashes()){
            KVStorageNode node = metaData.getStorageNodeFromHash(key);
            if(node.getHashRange().inRange(hash)){
                return node;
            }
        }
        return null;
    }

    public KVStorageNode getResponsibleStorageNode(String key){
        return getResponsibleStorageNode(hash(key));
    }

    public List<KVStorageNode> getStorageNodes(){
        return metaData.getStorageNodes();
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
     * Add a new NetworkNode into the location
     * @param node new KVNetworkNode;
     */
    public void addStorageNode(KVStorageNode node) {
        BigInteger hash = hash(node.getUID());
        if(this.metaData==null){
            update(new KVMetadata());
        }
        if(!metaData.hasStorageNodeByHash(hash)){
            this.metaData.addStorageNodeHashPair(hash,node);
            this.keys.add(hash);
            generateHashRange();
        }
        else{
            this.metaData.addStorageNodeHashPair(hash,node);
        }
    }

    public void removeStorageNode(String UID){
        BigInteger hash = hash(UID);
        if(this.metaData==null){
            update(new KVMetadata());
        }
        if(metaData.hasStorageNodeByHash(hash)){
            this.metaData.removeStorageNodeHashPair(hash);
            this.keys.remove(hash);
            generateHashRange();
        }
    }

    public void addStorageNodes(List<KVStorageNode> nodes){
        if(this.metaData == null){
            update(new KVMetadata());
        }
        for (KVStorageNode node: nodes
             ) {
            BigInteger hash = hash(node.getUID());
            if(!metaData.hasStorageNodeByHash(hash)){
                this.metaData.addStorageNodeHashPair(hash,node);
                this.keys.add(hash);
            }
        }
        generateHashRange();
    }

    public void clearStorageNodes(){
        if(metaData!=null)
    		metaData.clear();
        if(keys!=null)
        keys.clear();
    }

    public void setPrimary(String clusterUID, String serverUID){
        KVStorageNode node = getStorageNode(clusterUID);
        if(node.getNodeType()== eKVNetworkNodeType.STORAGE_CLUSTER){
            KVStorageCluster cluster = (KVStorageCluster)node;
            cluster.setPrimaryNodeUID(serverUID);
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

    private void generateHashRange(){
        Iterator<BigInteger> itor = keys.iterator();
        if(itor.hasNext()){
            BigInteger previous = itor.next();
            while(itor.hasNext()){
                BigInteger current = itor.next();
                metaData.getStorageNodeFromHash(previous).setHashRange(new KVRange<>(previous,current.subtract(BigInteger.valueOf(1)),true,true));
                previous = current;
            }
            metaData.getStorageNodeFromHash(previous).setHashRange(new KVRange<>(previous,keys.first().subtract(BigInteger.valueOf(1)),true,true));
        }
    }

    public KVStorageNode getStorageNode(String UID){
        return this.metaData.getStorageNodeFromHash(this.hash(UID));
    }
}
