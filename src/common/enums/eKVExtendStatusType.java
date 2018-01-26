package common.enums;

import common.messages.KVMessage;

public enum eKVExtendStatusType {
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
    NO_RESPONSE(15),
    UNKNOWN_ERROR(0);
    private final int value;

    /**
     * Construct the StatusType and correlate it to an specific integer
     * @param value
     */
    private eKVExtendStatusType(int value){
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
    public static eKVExtendStatusType fromInt(int input){
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
            case 15: return NO_RESPONSE;
        }
        return null;
    }


    public KVMessage.StatusType toStatusType(){
        switch (this.value){
            case 1: return KVMessage.StatusType.GET;
            case 2: return KVMessage.StatusType.GET_ERROR;
            case 3: return KVMessage.StatusType.GET_SUCCESS;
            case 4: return KVMessage.StatusType.PUT;
            case 5: return KVMessage.StatusType.PUT_SUCCESS;
            case 6: return KVMessage.StatusType.PUT_UPDATE;
            case 7: return KVMessage.StatusType.PUT_ERROR;
            case 8: return KVMessage.StatusType.DELETE_SUCCESS;
            case 9: return KVMessage.StatusType.DELETE_ERROR;
        }
        return null;
    }

    public static eKVExtendStatusType fromStatusType(KVMessage.StatusType type){
        switch (type){
            case GET: return eKVExtendStatusType.GET;
            case GET_ERROR: return eKVExtendStatusType.GET_ERROR;
            case GET_SUCCESS: return eKVExtendStatusType.GET_SUCCESS;
            case PUT: return eKVExtendStatusType.PUT;
            case PUT_SUCCESS: return eKVExtendStatusType.PUT_SUCCESS;
            case PUT_UPDATE: return eKVExtendStatusType.PUT_UPDATE;
            case PUT_ERROR: return eKVExtendStatusType.PUT_ERROR;
            case DELETE_SUCCESS: return eKVExtendStatusType.DELETE_SUCCESS;
            case DELETE_ERROR: return eKVExtendStatusType.DELETE_ERROR;
        }
        return  null;
    }
}