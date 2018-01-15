package common.messages;
import org.json.*;

import java.util.HashMap;
import java.util.Map;

public class KVJSONMessage implements KVMessage {

	private String key;
	private String Value;
	private StatusType status;
	private static String KEY_PAIR_NAME = "key_pair";
	private static String STATUS_NAME = "status_type";

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return Value;
	}
	

	@Override
	public StatusType getStatus() {
		return status;
	}
	
	@Override
	public byte[] toBytes() {
		JSONObject newObject = new JSONObject();
        Map<String, String> newmap = new HashMap<String, String>();
        newmap.put(key,Value);
		newObject.put(KEY_PAIR_NAME,newmap);
		newObject.put(STATUS_NAME,status.getValue());
		return newObject.toString().getBytes();
	}

	@Override
	public KVMessage fromBytes(byte[] in_Bytes) throws IllegalArgumentException {
        JSONObject keypair;
        JSONObject newObject;
        try{
            newObject = new JSONObject(new String(in_Bytes));
            keypair =  newObject.getJSONObject(KEY_PAIR_NAME);
        }
        catch (JSONException e){
            throw new IllegalArgumentException();
        }
		int new_status = newObject.getInt(STATUS_NAME);
		if(keypair == null){
		    throw new IllegalArgumentException();
        }
        else{
            key =  keypair.keys().next();
            Value = keypair.getString(key);
            status = StatusType.fromInt(new_status);
        }
		return this;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public void setValue(String value) {
		this.Value = value;
	}

	@Override
	public void setStatus(StatusType inType) {
		this.status = inType;
	}

}
