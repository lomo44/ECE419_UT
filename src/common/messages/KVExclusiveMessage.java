package common.messages;

import common.enums.eKVExtendStatusType;
import org.json.JSONObject;

import java.util.HashMap;

public class KVExclusiveMessage {
    HashMap<String, String> entries;
    String identifier;
    String payloadID;
    public KVExclusiveMessage(String identifier, String payloadID){
        this.identifier = identifier;
        this.payloadID = payloadID;
        entries = new HashMap<>();
    }

    public boolean loadFromKVJSONMessage(KVJSONMessage message){
        if(message.getKey().matches(identifier)){
            String payloadString = message.getValue();
            JSONObject payload = new JSONObject(payloadString);
            if(payload.has(payloadID)){
                JSONObject entries = payload.getJSONObject(payloadID);
                for (String key: entries.keySet()
                        ) {
                    this.add(key,entries.getString(key));
                }
                return true;
            }
        }
        return false;
    }

    public void add(String key, String payload){
        entries.put(key,payload);
    }
    public String get(String key){
        return entries.get(key);
    }

    public HashMap<String, String> getEntries() {
        return entries;
    }

    public KVJSONMessage toKVJSONMessage(){
        KVJSONMessage ret = new KVJSONMessage();
        ret.setKey(identifier);
        JSONObject payload = new JSONObject();
        payload.put(payloadID, entries);
        ret.setValue(payload.toString());
        ret.setExtendStatus(eKVExtendStatusType.MIGRATION_DATA);
        return ret;
    }
}
