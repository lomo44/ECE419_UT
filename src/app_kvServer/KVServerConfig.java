package app_kvServer;


import common.messages.KVExclusiveMessage;
import common.messages.KVJSONMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class KVServerConfig extends KVExclusiveMessage {
    public final static String KVCONFIGRATION_KEY = "e12c8b50-2089-11e8-9066-539b234d1994";
    public final static String KVCONFIGRATION_PAYLOADID= "e92c6cd0-2089-11e8-84ec-b367095a8790";

    public final static String KEY_CACHE_STRATAGY = "cache_stratagy";
    public final static String KEY_CACHE_SIZE = "cache_size";
    public final static String KEY_SERVER_PORT = "server_port";
    public final static String KEY_SERVER_HOST = "server_host";
    public final static String KEY_SERVER_NAME = "server_name";
    public final static String KEY_BELONGED_CLUSTER = "belonged_cluster";
    public static final String KEY_BELONGED_CLUSTER_TAG = "clusters";

    public KVServerConfig() {
        super(KVCONFIGRATION_KEY, KVCONFIGRATION_PAYLOADID);
    }

    public static KVServerConfig fromKVJSONMessage(KVJSONMessage msg){
        KVServerConfig ret = new KVServerConfig();
        if(ret.loadFromKVJSONMessage(msg)){
            return ret;
        }
        return null;
    }

    public String getCacheStratagy(){
        return get(KEY_CACHE_STRATAGY);
    }
    public void setCacheStratagy(String stratagy){
        put(KEY_CACHE_STRATAGY,stratagy);
    }

    public int getCacheSize(){
        return Integer.parseInt(get(KEY_CACHE_SIZE));
    }

    public void setCacheSize(int cacheSize){
        put(KEY_CACHE_SIZE,Integer.toString(cacheSize));
    }
    
    public int getServerPort() {
    		return Integer.valueOf(get(KEY_SERVER_PORT));
    }
    public void setServerPort(int port) {
        put(KEY_SERVER_PORT,Integer.toString(port));
    }

    public void setServerHostAddress(String host){
        put(KEY_SERVER_HOST,host);
    }
    public String getServerHostAddress(){
        return get(KEY_SERVER_HOST);
    }

    public void setServerName(String name) {put(KEY_SERVER_NAME,name);}
    public String getServerName(){
        return get(KEY_SERVER_NAME);
    }

    public void setBelongedCluster(Collection<String> clusterNames){
        JSONObject newObject =new JSONObject();
        newObject.put(KEY_BELONGED_CLUSTER_TAG,clusterNames);
        put(KEY_BELONGED_CLUSTER,newObject.toString());
    }
    public Collection<String> getBelongedCluster(){
        HashSet<String> hashSet = new HashSet<>();
        JSONObject newObject = new JSONObject(get(KEY_BELONGED_CLUSTER));
        JSONArray arrays = newObject.getJSONArray(KEY_BELONGED_CLUSTER_TAG);
        for(int i = 0 ; i <arrays.length(); i++){
            hashSet.add(arrays.getString(i));
        }
        return hashSet;
    }
}
