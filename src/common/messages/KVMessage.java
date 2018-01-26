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
		DELETE_ERROR(9), 	/* Delete - request successful */
		ECHO(10),
        CONNECT_SUCCESS(11),
        CONNECT_FAIL(12),
        DISCONNECT_SUCCESS(13),
        DISCONNECT_FAIL(14),
        NORESPONSE(15),
		UNKNOWN_ERROR(0);
		private final int value;

        /**
         * Construct the StatusType and correlate it to an specific integer
         * @param value
         */
		private StatusType(int value){
			this.value = value;
		}

        /**
         * Return the integer representation of this enumeration
         * @return
         */
		public int getValue(){
			return value;
		}

        /**
         * Convert the integer representation to a StatusType Object
         * @param input int - integer representation
         * @return StatusType object
         */
		public static StatusType fromInt(int input){
            switch (input){
				case 0: return UNKNOWN_ERROR;
                case 1: return GET;
                case 2: return GET_ERROR;
                case 3: return GET_SUCCESS;
                case 4: return PUT;
                case 5: return PUT_SUCCESS;
                case 6: return PUT_UPDATE;
				case 7: return PUT_ERROR;
                case 8: return DELETE_SUCCESS;
                case 9: return DELETE_ERROR;
				case 10: return ECHO;
                case 11: return CONNECT_SUCCESS;
                case 12: return CONNECT_FAIL;
                case 13: return DISCONNECT_SUCCESS;
                case 14: return DISCONNECT_FAIL;
                case 15: return NORESPONSE;
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
	 * @return a status string that is used to identify request types, 
	 * response types and error types associated to the message.
	 */
	public StatusType getStatus();

}


