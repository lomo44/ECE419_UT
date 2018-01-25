package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVMessage;
import logger.KVOut;

import java.util.HashMap;
import java.util.Map;

public abstract class KVCommand {
    private KVCommandPattern.KVCommandType commandType;
    private Map<String,String> parameterMap;
    private KVOut kv_out = new KVOut();
    public KVCommand(KVCommandPattern.KVCommandType commandType){
        this.commandType = commandType;
        parameterMap = new HashMap<>();
    }
    public void set(String parameter, String value){
        parameterMap.put(parameter,value);
    }
    public KVCommandPattern.KVCommandType getCommandType(){
        return commandType;
    }
    public String get(String key){
        return parameterMap.get(key);
    }
    public abstract KVMessage execute(KVClient clientInstance);
    public abstract void handleResponse(KVMessage response);
    public void printPrompt() {
        System.out.print("Client> ");
    }
}
