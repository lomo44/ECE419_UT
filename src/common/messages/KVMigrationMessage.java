package common.messages;

import org.json.JSONObject;

import java.util.HashMap;

public class KVMigrationMessage {
    public final static String KVMIGRATIONMESSAGE_IDENTIFIER = "MigrationMessageHAHA";
    public final static String KVMIGRATIONMESSAGE_PAYLOAD_ID = "Payload";
    HashMap<String, String> entries;
    public KVMigrationMessage(){
        entries = new HashMap<>();
    }

    /**
     * Add a key value pair to the migration message
     * @param key
     * @param value
     */
    public void add(String key, String value){
        entries.put(key,value);
    }

    /**
     * Retrieve the entries that this migration module contains
     * @return
     */
    public HashMap<String, String> getEntries() {
        return entries;
    }

    /**
     * return the value mapped to the key
     * @param key
     * @return
     */
    public String get(String key){
        return entries.get(key);
    }

    /**
     * Serialized the Migration message into KVJSONMessage
     * @return
     */
    public KVJSONMessage toKVJSONMessage(){
        KVJSONMessage ret = new KVJSONMessage();
        ret.setKey(KVMIGRATIONMESSAGE_IDENTIFIER);
        JSONObject payload = new JSONObject();
        payload.put(KVMIGRATIONMESSAGE_PAYLOAD_ID,entries);
        ret.setValue(payload.toString());
        return ret;
    }

    /**
     * De-serialize the KVJSONMessage and produce a KVMigration message
     * @param message json message
     * @return migration message
     */
    public static KVMigrationMessage fromKVJSONMessage(KVJSONMessage message){
        KVMigrationMessage ret = null;
        if(message.getKey().matches(KVMIGRATIONMESSAGE_IDENTIFIER)){
            ret = new KVMigrationMessage();
            String payloadString = message.getValue();
            JSONObject payload = new JSONObject(payloadString);
            if(payload.has(KVMIGRATIONMESSAGE_PAYLOAD_ID)){
                JSONObject entries = payload.getJSONObject(KVMIGRATIONMESSAGE_PAYLOAD_ID);
                for (String key: entries.keySet()
                     ) {
                    ret.add(key,entries.getString(key));
                }
            }
        }
        return ret;
    }
}
