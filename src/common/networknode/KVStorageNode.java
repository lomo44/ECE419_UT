package common.networknode;

import app_kvServer.KVServerConfig;
import common.datastructure.KVRange;
import common.enums.eKVNetworkNodeType;
import database.storage.KVStorage;
import ecs.ECSNode;
import ecs.IECSNode;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Objects;
import java.util.regex.Matcher;

public class KVStorageNode extends KVNetworkNode{
    private static final String JSON_HASHRANGE_KEY = "hash_range";
    private KVRange<BigInteger> hashRange = new KVRange<>(BigInteger.valueOf(0),BigInteger.valueOf(0),false,false);
    /**
     * Create a network id from hostname and port number
     *
     * @param hostname   String representation of host name
     * @param portNumber Integer representation of port number
     */
    public KVStorageNode(String hostname, int portNumber, String servername) {
        super(hostname, portNumber,servername);
        this.nodeType = eKVNetworkNodeType.STORAGE_NODE;
    }

    public KVStorageNode(KVServerConfig config){
        this(config.getServerHostAddress(),config.getServerPort(),config.getServerName());
    }

    public KVStorageNode(KVNetworkNode node){
        this(node.getHostName(),node.getPortNumber(),node.UID);
    }

    @Override
    public boolean equals(Object o) {
        boolean ret = true;
        KVStorageNode rhs = (KVStorageNode)(o);
        if(this.hashRange!=null && rhs!=null){
            ret &= this.hashRange.equals(rhs.hashRange);
        }
        else if(this.hashRange == null && rhs.hashRange ==null){
            ret &= true;
        }
        else{
            return false;
        }
        return super.equals(o) && ret;
    }

    @Override
    public int hashCode() {
        return getUID().hashCode();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject object = super.toJSONObject();
        object.put(JSON_HASHRANGE_KEY,hashRange.toJSONObject());
        return object;
    }

    public static KVStorageNode fromJSONObject(JSONObject obj){
        if(obj.has(JSON_NODETYPE_KEY)){
            KVNetworkNode  network_node = KVNetworkNode.fromJSONObject(obj);
            KVStorageNode node = new KVStorageNode(network_node);
            JSONObject hashRangeObject = obj.getJSONObject(JSON_HASHRANGE_KEY);
            node.setHashRange(KVRange.fromJSONObject(hashRangeObject));
            node.setNodeType(eKVNetworkNodeType.fromInt(obj.getInt(JSON_NODETYPE_KEY)));
            return node;
        }
        return null;
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
