package common.enums;

import common.KVMessage;

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
    SERVER_STOPPED(16),         /* Server is stopped, no requests are processed */
    SERVER_WRITE_LOCK(17),      /* Server locked for out, only get possible */
    SERVER_NOT_RESPONSIBLE(18),  /* Request not successful, server not responsible for key */
    METADATA_UPDATE(19),
    MIGRATION_COMPLETE(20),
    MIGRATION_DATA(21),
    MIGRATION_INCOMPLETE(22),
    ADD_NODE_SUCCESS(23),
    ADD_NODE_FAIL(24),
    SETUP_NODE_SUCCESS(25),
    SETUP_NODE_FAIL(26),
    REMOVE_NODE_SUCCESS(27),
    REMOVE_NODE_FAIL(28),
    GET_NODE_SUCCESS(29),
    GET_NODE_FAIL(30),
    SHUTDOWN_SUCCESS(31),
    SHUTDOWN_FAIL(32),
    START_SUCCESS(33),
    START_FAIL(34),
    STOP_SUCCESS(35),
    STOP_FAIL(36),
    SERVER_START(37),
    SERVER_STOP(38),
    SERVER_SHUTDOWN(39),
    CLEAR_STORAGE(40),
    CLEAR_SUCCESS(41),
    CLEAR_FAILED(42),
    PRIMARY_UPDATE(43),
    PRIMARY_DECLARE(44),
    REPLICA_OK(45),
    REPLICA_ERROR(46),
    CLUSTER_OPERATION(47),
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
            case 16: return SERVER_STOPPED;
            case 17: return SERVER_WRITE_LOCK;
            case 18: return SERVER_NOT_RESPONSIBLE;
            case 19: return METADATA_UPDATE;
            case 20: return MIGRATION_COMPLETE;
            case 21: return MIGRATION_DATA;
            case 22: return MIGRATION_INCOMPLETE;
            case 31: return SHUTDOWN_SUCCESS;
            case 32: return SHUTDOWN_FAIL;
            case 33: return START_SUCCESS;
            case 34: return START_FAIL;
            case 35: return STOP_SUCCESS;
            case 36: return STOP_FAIL;
            case 37: return SERVER_START;
            case 38: return SERVER_STOP;
            case 39: return SERVER_SHUTDOWN;
            case 40: return CLEAR_STORAGE;
            case 41: return CLEAR_SUCCESS;
            case 42: return CLEAR_FAILED;
            case 43: return PRIMARY_UPDATE;
            case 44: return PRIMARY_DECLARE;
            case 45: return REPLICA_OK;
            case 46: return REPLICA_ERROR;
            case 47: return CLUSTER_OPERATION;

        }
        return null;
    }


    /**
     * Cast the extended status type to normal status type
     * @return
     */
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
            case 16: return KVMessage.StatusType.SERVER_STOPPED;
            case 17: return KVMessage.StatusType.SERVER_WRITE_LOCK;
            case 18: return KVMessage.StatusType.SERVER_NOT_RESPONSIBLE;
        }
        return null;
    }

    /**
     * Create a extended status type from normal status type
     * @param type
     * @return
     */
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
            case SERVER_STOPPED: return eKVExtendStatusType.SERVER_STOPPED;
            case SERVER_WRITE_LOCK: return eKVExtendStatusType.SERVER_WRITE_LOCK;
            case SERVER_NOT_RESPONSIBLE: return eKVExtendStatusType.SERVER_NOT_RESPONSIBLE;
        }
        return  null;
    }
}