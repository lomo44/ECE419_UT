package common.networknode;

import common.datastructure.KVRange;

import java.math.BigInteger;
import java.util.regex.Matcher;

public class KVStorageNode extends KVNetworkNode{
    private KVRange<BigInteger> hashRange;

    /**
     * Create a network id from hostname and port number
     *
     * @param hostname   String representation of host name
     * @param portNumber Integer representation of port number
     */
    public KVStorageNode(String hostname, int portNumber) {
        super(hostname, portNumber);
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
            return false;
        }
    }

    public static KVStorageNode fromString(String str){
        Matcher match = re_pattern.matcher(str);
        if(match.matches()){
            return new KVStorageNode(match.group(1),Integer.parseInt(match.group(2)));
        }
        return null;
    }
}
