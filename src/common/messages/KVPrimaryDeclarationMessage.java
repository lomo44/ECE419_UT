package common.messages;

public class KVPrimaryDeclarationMessage extends KVExclusiveMessage {
    private final static String KVPRIMARYDECLARATIONMESSAGE_IDENTIFIER = "ab50dc9e-2d8f-11e8-b0cb-db8b11a88dfd";
    private final static String KVPRIMARYDECLARATIONMESSAGE_PAYLOADID = "c78834d4-2d8f-11e8-ba51-53ce7bd2fd3f";
    private final static String KVPRIMARYDECLARATIONMESSAGE_CLUSTERID = "cluster_id";
    private final static String KVPRIMARYDECLARATIONMESSAGE_PRIMARYID = "primary_id";
    public KVPrimaryDeclarationMessage() {
        super(KVPRIMARYDECLARATIONMESSAGE_IDENTIFIER, KVPRIMARYDECLARATIONMESSAGE_PAYLOADID);
    }
    public static KVPrimaryDeclarationMessage fromKVJSONMessage(KVJSONMessage msg){
        KVPrimaryDeclarationMessage ret = new KVPrimaryDeclarationMessage();
        if(ret.loadFromKVJSONMessage(msg)){
            return ret;
        }
        else{
            return null;
        }
    }
    public void setClusterID(String clusterID){
        put(KVPRIMARYDECLARATIONMESSAGE_CLUSTERID,clusterID);
    }
    public String getClusterID(){
        return get(KVPRIMARYDECLARATIONMESSAGE_CLUSTERID);
    }
    public void setPrimaryID(String primaryID){
        put(KVPRIMARYDECLARATIONMESSAGE_PRIMARYID,primaryID);
    }
    public String getPrimaryID(){
        return get(KVPRIMARYDECLARATIONMESSAGE_PRIMARYID);
    }
}
