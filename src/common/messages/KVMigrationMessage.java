package common.messages;

import common.enums.eKVExtendStatusType;
import org.json.JSONObject;

import java.util.HashMap;

public class KVMigrationMessage extends KVExclusiveMessage{
    public final static String KVMIGRATIONMESSAGE_IDENTIFIER = "539a5ed2-a118-40b6-8981-b7e4878ced1b";
    public final static String KVMIGRATIONMESSAGE_PAYLOAD_ID = "cc1046ba-8949-4f0e-a7f9-ea0597c1de25";
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
}
