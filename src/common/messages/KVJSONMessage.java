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

    /**
     * Return key as a string
     * @return key (String)
     */
	@Override
	public String getKey() {
		return key;
	}

    /**
     * Return value as a string
     * @return value (string)
     */
	@Override
	public String getValue() {
		return Value;
	}

    /**
     * Return status type of the message
     * @return StatusType
     */
	@Override
	public StatusType getStatus() {
		return status;
	}

    /**
     * Serialize the object into bytes array
     * @return byte[]
     */
	@Override
	public byte[] toBytes() {
		JSONObject newObject = new JSONObject();
        Map<String, String> newmap = new HashMap<String, String>();
        newmap.put(key,Value);
		newObject.put(KEY_PAIR_NAME,newmap);
		newObject.put(STATUS_NAME,status.getValue());
		return newObject.toString().getBytes();
	}

    /**
     * De-serialize the incoming byte array
     * @param in_Bytes incoming byte array
     * @return base class KVMessage
     * @throws IllegalArgumentException If the incoming byte array is not valid
     */
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

		    if(keypair.keys().hasNext()) {
                key = keypair.keys().next();
                Value = keypair.getString(key);
            }
            status = StatusType.fromInt(new_status);
        }
		return this;
	}

	@Override
	public boolean equal(KVMessage msg) {
        return this.Value.equals(msg.getValue()) &&
				this.key.equals(msg.getKey()) &&
				this.status.getValue()== msg.getStatus().getValue();
	}

	/**
     * Set the key of the message
     * @param key String
     */
	@Override
	public void setKey(String key) {
		this.key = key;
	}

    /**
     * Set the value of the message
     * @param value String
     */
	@Override
	public void setValue(String value) {
		this.Value = value;
	}

    /** Set the status of the message
     * @param inType StatusType
     */
	@Override
	public void setStatus(StatusType inType) {
		this.status = inType;
	}

}