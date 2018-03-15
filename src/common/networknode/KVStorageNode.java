package common.networknode;

import common.datastructure.KVRange;
import database.storage.KVStorage;
import ecs.ECSNode;
import ecs.IECSNode;

import java.math.BigInteger;
import java.util.regex.Matcher;

public class KVStorageNode extends KVNetworkNode{
    private KVRange<BigInteger> hashRange;
    private String serverName ;
    /**
     * Create a network id from hostname and port number
     *
     * @param hostname   String representation of host name
     * @param portNumber Integer representation of port number
     */
    public KVStorageNode(String hostname, int portNumber, String servername) {
        super(hostname, portNumber,servername);
    }

    public KVStorageNode(KVNetworkNode node){
        super(node.getHostName(),node.getPortNumber(),node.UID);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


    public KVRange<BigInteger> getHashRange() {
        return hashRange;
    }

    public void setHashRange(KVRange<BigInteger> hashRange) {
        this.hashRange = hashRange;
    }


    public boolean isResponsible(BigInteger hash){
        if(hashRange!=null){
            return hashRange.inRange(hash);
        }
        else {
            return true;
        }
    }

    public static KVStorageNode fromString(String str){
        Matcher match = re_pattern.matcher(str);
        if(match.matches()){
            return new KVStorageNode(match.group(1),Integer.parseInt(match.group(2)), match.group(3));
        }
        return null;
    }

    public ECSNode toECSNode(){
        return new ECSNode(this);
    }
}
