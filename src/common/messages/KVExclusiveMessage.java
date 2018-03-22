package common.messages;

import common.enums.eKVExtendStatusType;
import org.json.JSONObject;

import java.util.HashMap;

public class KVExclusiveMessage extends HashMap<String,String>{
    String identifier;
    String payloadID;
    public KVExclusiveMessage(String identifier, String payloadID){
        this.identifier = identifier;
        this.payloadID = payloadID;
    }

    public boolean loadFromKVJSONMessage(KVJSONMessage message){
        if(message.getKey().matches(identifier)){
            String payloadString = message.getValue();
            JSONObject payload = new JSONObject(payloadString);
            if(payload.has(payloadID)){
                JSONObject entries = payload.getJSONObject(payloadID);
                for (String key: entries.keySet()
                        ) {
                    this.put(key,entries.getString(key));
                }
                return true;
            }
        }
        return false;
    }
    public KVJSONMessage toKVJSONMessage(){
        KVJSONMessage ret = new KVJSONMessage();
        ret.setKey(identifier);
        JSONObject payload = new JSONObject();
        payload.put(payloadID, this);
        ret.setValue(payload.toString());
        ret.setExtendStatus(eKVExtendStatusType.UNKNOWN_ERROR);
        return ret;
    }
}
