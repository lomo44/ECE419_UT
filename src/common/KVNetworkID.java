package common;

import common.datastructure.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVNetworkID {
    private Pair<String, Integer> id = new Pair<>("",0);
    private static final Pattern re_pattern = Pattern.compile("(.*)@(\\d*)");

    /**
     * Create a network id from hostname and port number
     * @param hostname String representation of host name
     * @param portNumber Integer representation of port number
     */
    public KVNetworkID(String hostname, int portNumber){
        id.x = hostname;
        id.y = portNumber;
    }

    /**
     * Convert ID into string representation
     * @return
     */
    public String toString(){
        return id.x +'@'+ Integer.toString(id.y);
    }

    /**
     * Convert string generated from toString() to KVNetworkID
     * @param str Input string
     * @return KVNetworkID instance
     */
    public static KVNetworkID fromString(String str){
        Matcher match = re_pattern.matcher(str);
        if(match.matches()){
            return new KVNetworkID(match.group(1),Integer.parseInt(match.group(2)));
        }
        return null;
    }

    /**
     * Return the host name of the network ID
     * @return host name
     */
    public String getHostName(){return id.x;}

    /**
     * Return the portnumber of the network ID
     * @return
     */
    public int getPortNumber(){return id.y;}

    /**
     * Check if input KVNetworkID is the same as the current one
     * @param o input KVNetworkID instance
     * @return true if the input instance is the same as the current one
     */
    @Override
    public boolean equals(Object o) {
        KVNetworkID rhs = (KVNetworkID) o;
        return this.id.x.matches(rhs.id.x ) && this.id.y == rhs.id.y;
    }
}
