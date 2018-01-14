package common.messages;

public interface KVMessage {
	
	public enum StatusType {
		GET(1), 			/* Get - request */
		GET_ERROR(2), 		/* requested tuple (i.e. value) not found */
		GET_SUCCESS(3), 	/* requested tuple (i.e. value) found */
		PUT(4), 			/* Put - request */
		PUT_SUCCESS(5), 	/* Put - request successful, tuple inserted */
		PUT_UPDATE(6), 	/* Put - request successful, i.e. value updated */
		PUT_ERROR(7), 		/* Put - request not successful */
		DELETE_SUCCESS(8), /* Delete - request successful */
		DELETE_ERROR(9); 	/* Delete - request successful */
		private final int value;
		private StatusType(int value){
			this.value = value;
		}
		public int getValue(){
			return value;
		}
		public static StatusType fromInt(int input){
            switch (input){
                case 1: return GET;
                case 2: return GET_ERROR;
                case 3: return GET_SUCCESS;
                case 4: return PUT;
                case 5: return PUT_SUCCESS;
                case 6: return PUT_ERROR;
                case 7: return DELETE_SUCCESS;
                case 8: return DELETE_ERROR;
            }
            return null;
		}
	}

	/**
	 * @return the key that is associated with this message, 
	 * 		null if not key is associated.
	 */
	public String getKey();
	
	/**
	 * @return the value that is associated with this message, 
	 * 		null if not value is associated.
	 */
	public String getValue();
	
	/**
	 * Set the key
	 * @param key
	 */
	public void setKey(String key);
	/**
	 * Set the value
	 * @param value
	 */
	public void setValue(String value);
	
	/**
	 * @return a status string that is used to identify request types, 
	 * response types and error types associated to the message.
	 */
	public StatusType getStatus();
	
	/**
	 * Set the status of the message
	 * @param inType
	 */
	public void setStatus(StatusType inType);
	public byte[] toBytes();
	public KVMessage fromBytes(byte[] in_Bytes);
}


