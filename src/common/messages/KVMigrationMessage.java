package common.messages;

import common.enums.eKVExtendStatusType;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class KVMigrationMessage extends KVExclusiveMessage{
    public final static String KVMIGRATIONMESSAGE_IDENTIFIER = "539a5ed2-a118-40b6-8981-b7e4878ced1b";
    public final static String KVMIGRATIONMESSAGE_PAYLOAD_ID = "cc1046ba-8949-4f0e-a7f9-ea0597c1de25";
    public final static String KVMIGRATIONMESSAGE_TARGET_NAME = "target_name";
    public final static String KVMIGRATIONMESSAGE_TARGET_DATA = "target_data";
    public KVMigrationMessage(){
        super(KVMIGRATIONMESSAGE_IDENTIFIER,KVMIGRATIONMESSAGE_PAYLOAD_ID);
    }
    public static KVMigrationMessage fromKVJSONMessage(KVJSONMessage msg){
        KVMigrationMessage ret = new KVMigrationMessage();
        if(ret.loadFromKVJSONMessage(msg)){
            return ret;
        }
        else{
            return null;
        }
    }
    public void setTargetName(String targetName){
        this.put(KVMIGRATIONMESSAGE_TARGET_NAME,targetName);
    }
    public String getTargetNodeUID(){
        return this.get(KVMIGRATIONMESSAGE_TARGET_NAME);
    }
    public HashMap<String,String> getEntries(){
        HashMap<String,String> ret = new HashMap<>();
        JSONObject object = new JSONObject(this.get(KVMIGRATIONMESSAGE_TARGET_DATA));
        Set<String> keys = object.keySet();
        for(String key : keys){
            ret.put(key,object.getString(key));
        }
        return ret;
    }
    public void setEntries(HashMap<String, String> entries){
        JSONObject entriesObject = new JSONObject(entries);
        this.put(KVMIGRATIONMESSAGE_TARGET_DATA,entriesObject.toString());
    }
}
