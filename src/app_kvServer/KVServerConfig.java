package app_kvServer;


import common.messages.KVExclusiveMessage;
import common.messages.KVJSONMessage;
public class KVServerConfig extends KVExclusiveMessage {
    public final static String KVCONFIGRATION_KEY = "e12c8b50-2089-11e8-9066-539b234d1994";
    public final static String KVCONFIGRATION_PAYLOADID= "e92c6cd0-2089-11e8-84ec-b367095a8790";

    public final static String KEY_CACHE_STRATAGY = "cache_stratagy";
    public final static String KEY_CACHE_SIZE = "cache_size";
    public final static String KEY_SERVER_PORT = "server_port";
    public final static String KEY_SERVER_HOST = "server_host";

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
        add(KEY_CACHE_STRATAGY,stratagy);
    }

    public int getCacheSize(){
        return Integer.parseInt(get(KEY_CACHE_SIZE));
    }

    public void setCacheSize(int cacheSize){
        add(KEY_CACHE_SIZE,Integer.toString(cacheSize));
    }
    
    public int getServerPort() {
    		return Integer.valueOf(get(KEY_SERVER_PORT));
    }
    public void setServerPort(int port) {
    		add(KEY_SERVER_PORT,Integer.toString(port));
    }

    public void setServerHostName(String host){
        add(KEY_SERVER_HOST,host);
    }
    public String getServerHost(){
        return get(KEY_SERVER_HOST);
    }
}
