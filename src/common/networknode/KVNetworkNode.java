package common.networknode;

import common.communication.KVCommunicationModule;
import common.datastructure.Pair;

import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVNetworkNode {
    private Pair<String, Integer> id = new Pair<>("",0);
    protected static final Pattern re_pattern = Pattern.compile("(.*) (\\d*)");

    /**
     * Create a network id from hostname and port number
     * @param hostname String representation of host name
     * @param portNumber Integer representation of port number
     */
    public KVNetworkNode(String hostname, int portNumber){
        id.x = hostname;
        id.y = portNumber;
    }

    /**
     * Convert ID into string representation
     * @return
     */
    @Override
    public String toString(){
        return id.x +' '+ Integer.toString(id.y);
    }

    /**
     * Convert string generated from toString() to KVNetworkNode
     * @param str Input string
     * @return KVNetworkNode instance
     */
    public static KVNetworkNode fromString(String str){
        Matcher match = re_pattern.matcher(str);
        if(match.matches()){
            return new KVNetworkNode(match.group(1),Integer.parseInt(match.group(2)));
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
     * Check if input KVNetworkNode is the same as the current one
     * @param o input KVNetworkNode instance
     * @return true if the input instance is the same as the current one
     */
    @Override
    public boolean equals(Object o) {
        KVNetworkNode rhs = (KVNetworkNode) o;
        return this.id.x.matches(rhs.id.x ) && this.id.y.equals(rhs.id.y);
    }

    /**
     * Generate the hash code for the current NetworkNode
     * @return
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * Create socket based on the network name and port number
     * @return Socket instance if successfully created
     * @throws IOException
     */
    public Socket createSocket() throws IOException {
        return new Socket(this.getHostName(),this.getPortNumber());
    }

    public KVCommunicationModule createCommunicationModule() throws IOException {
        return new KVCommunicationModule(createSocket(),toString());
    }
}
