package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVJSONMessage;
import common.messages.KVMessage;
import logger.KVOut;

import java.util.HashMap;
import java.util.Map;

/**
 * Interfaces for KVCommand, use this class to implement more command for CLI
 */
public abstract class KVCommand {
    private KVCommandPattern.KVCommandType commandType;
    private Map<String,String> parameterMap;
    protected KVOut kv_out = new KVOut();


    public KVCommand(KVCommandPattern.KVCommandType commandType){
        this.commandType = commandType;
        parameterMap = new HashMap<>();
    }

    /**
     * Set a parameter with its corresponded value
     * @param parameter
     * @param value
     */
    public void set(String parameter, String value){
        parameterMap.put(parameter,value);
    }

    /**
     * Get command type
     * @return
     */
    public KVCommandPattern.KVCommandType getCommandType(){
        return commandType;
    }

    /**
     * Get parameter value using key
     * @param key
     * @return
     */
    public String getValue(String key){
        return parameterMap.get(key);
    }

    /**
     * Command execute handler.
     * @param clientInstance Client instance
     * @return return message from CLI
     */
    public abstract KVJSONMessage execute(KVClient clientInstance);

    /**
     * Handle the response message
     * @param response response message return from execution
     */
    public abstract void handleResponse(KVJSONMessage response);
}
