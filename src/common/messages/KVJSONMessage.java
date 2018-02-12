package common.messages;
import common.enums.eKVExtendStatusType;
import ecs.IECSNode;
import org.json.*;

import java.util.HashMap;
import java.util.Map;

public class KVJSONMessage implements KVMessage {

	private String key = "";
	private String Value = "";
	private long sendTime = -1;
	private StatusType status;
	private eKVExtendStatusType extendStatusType;

	private static String KEY_PAIR_NAME = "key_pair";
	private static String STATUS_NAME = "status_type";
	private static String SEND_TIME = "send_time";
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

	@Override
	public IECSNode getResponsibleServer() {
		return null;
	}

	public eKVExtendStatusType getExtendStatusType(){
		return extendStatusType;
	}
    /**
     * Serialize the object into bytes array
     * @return byte[]
     */
	public byte[] toBytes() {
		JSONObject newObject = new JSONObject();
        Map<String, String> newmap = new HashMap<String, String>();
        newmap.put(key,Value);
		newObject.put(KEY_PAIR_NAME,newmap);
		newObject.put(STATUS_NAME, extendStatusType.getValue());
		newObject.put(SEND_TIME, sendTime);
		return newObject.toString().getBytes();
	}

    /**
     * De-serialize the incoming byte array
     * @param in_Bytes incoming byte array
     * @return base class KVMessage
     * @throws IllegalArgumentException If the incoming byte array is not valid
     */
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
                sendTime = newObject.getLong(SEND_TIME);
            }
            setExtendStatus(eKVExtendStatusType.fromInt(new_status));

        }
		return this;
	}

	public boolean equal(KVMessage msg) {
        return this.Value.equals(msg.getValue()) &&
				this.key.equals(msg.getKey()) &&
				this.status == msg.getStatus();
	}

	/**
     * Set the key of the message
     * @param key String
     */
	public void setKey(String key) {
		this.key = key;
	}

    /**
     * Set the value of the message
     * @param value String
     */
	public void setValue(String value) {
		this.Value = value;
	}

    /** Set the status of the message
     * @param inType StatusType
     */
	public void setStatus(StatusType inType) {
		this.status = inType;
		this.extendStatusType = eKVExtendStatusType.fromStatusType(inType);
	}

	/**
	 * Set the extended status of the message
	 * @param inType extended status
	 */
	public void setExtendStatus(eKVExtendStatusType inType){
		this.status = inType.toStatusType();
		this.extendStatusType = inType;
	}

	/**
	 * Set the send time of the KVMessage to the current time
	 */
	public void setSendTime() {
		this.sendTime = System.currentTimeMillis();
	}

	/**
	 * return the send time of this KVMessage
	 * @return
	 */
	public long getSendTime() {
		return this.sendTime;
	}

}
