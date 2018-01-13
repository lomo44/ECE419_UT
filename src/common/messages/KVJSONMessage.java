package common.messages;
import org.json.*;

public class KVJSONMessage implements KVMessage {

	private String key;
	private String Value;
	private StatusType status;
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return Value;
	}
	

	@Override
	public StatusType getStatus() {
		// TODO Auto-generated method stub
		return status;
	}
	
	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KVMessage fromBytes(byte[] in_Bytes) {
		// TODO Auto-generated method stub
		return null;
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
