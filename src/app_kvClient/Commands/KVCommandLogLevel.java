package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVMessage;

public class KVCommandLogLevel extends KVCommand {
    public KVCommandLogLevel() {
        super(KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Override
    public KVMessage execute(KVClient clientInstance) {
        clientInstance.setLevel(getLevel());
        System.out.println("Log Level: "+clientInstance.setLevel(getLevel()));
        return null;
    }

    @Override
    public void handleResponse(KVMessage response) {
        //ignore
    }

    public void setLevel(String level){
        set("Level",level);
    }

    public String getLevel(){
        return get("Level");
    }
}
