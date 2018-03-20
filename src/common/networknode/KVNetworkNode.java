package common.networknode;

import common.communication.KVCommunicationModule;
import common.datastructure.Pair;
import common.enums.eKVNetworkNodeType;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVNetworkNode {
    protected static final String JSON_NODETYPE_KEY = "node_type";
    private static final String JSON_HOSTNAME_KEY = "host_name";
    private static final String JSON_PORTNUMBER_KEY = "port_number";
    private static final String JSON_UID_KEY = "uid";
    protected static final Pattern re_pattern = Pattern.compile("(.*) (\\d*) (.*)");

    private Pair<String, Integer> id = new Pair<>("",0);
    protected String UID;
    protected eKVNetworkNodeType nodeType = eKVNetworkNodeType.NETWORK_NODE;

    /**
     * Create a network id from hostname and port number
     * @param hostname String representation of host name
     * @param portNumber Integer representation of port number
     */
    public KVNetworkNode(String hostname, int portNumber, String UID){
        id.x = hostname;
        id.y = portNumber;
        this.UID = UID;
    }

    /**
     * Convert ID into string representation
     * @return
     */
    @Override
    public String toString(){
        return String.format("%s %d %s",id.x,id.y,this.UID);
    }


    public JSONObject toJSONObject(){
        JSONObject ret = new JSONObject();
        ret.put(JSON_NODETYPE_KEY,nodeType.toInt());
        ret.put(JSON_HOSTNAME_KEY,this.getHostName());
        ret.put(JSON_PORTNUMBER_KEY,this.getPortNumber());
        ret.put(JSON_UID_KEY,this.getUID());
        return ret;
    }

    public static KVNetworkNode fromJSONObject(JSONObject object){
        if(object.has(JSON_NODETYPE_KEY)){
            return new KVNetworkNode(object.getString(JSON_HOSTNAME_KEY),
                    object.getInt(JSON_PORTNUMBER_KEY),
                    object.getString(JSON_UID_KEY));
        }
        return null;
    }

    /**
     * Convert string generated from toString() to KVNetworkNode
     * @param str Input string
     * @return KVNetworkNode instance
     */
    public static KVNetworkNode fromString(String str){
        Matcher match = re_pattern.matcher(str);
        if(match.matches()){
            return new KVNetworkNode(match.group(1),Integer.parseInt(match.group(2)), match.group(3));
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

    public String getUID() {
        return UID;
    }

    /**
     * Check if input KVNetworkNode is the same as the current one
     * @param o input KVNetworkNode instance
     * @return true if the input instance is the same as the current one
     */
    @Override
    public boolean equals(Object o) {
        KVNetworkNode rhs = (KVNetworkNode) o;
        return this.UID.matches(rhs.UID);
    }

    /**
     * Generate the hash code for the current NetworkNode
     * @return
     */
    @Override
    public int hashCode() {
        return this.UID.hashCode();
    }

    public eKVNetworkNodeType getNodeType(){
        return this.nodeType;
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
