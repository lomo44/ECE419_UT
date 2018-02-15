package app_kvClient.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVJSONMessage;

public class KVCommandLogLevel extends KVCommand<KVClient> {
    public KVCommandLogLevel() {
        super(KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Override
    public KVJSONMessage execute(KVClient clientInstance) {
        clientInstance.setLevel(getLevel());
        System.out.println("Log Level: "+clientInstance.setLevel(getLevel()));
        return null;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        //ignore
    }

    public void setLevel(String level){
        set("Level",level);
    }

    public String getLevel(){
        return getValue("Level");
    }
}
