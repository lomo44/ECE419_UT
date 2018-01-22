package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;

public class KVCommandLogLevel extends KVCommand {
    public KVCommandLogLevel() {
        super(KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Override
    public void execute(KVClient clientInstance) {
        clientInstance.setLevel(getLevel());
        System.out.println("Log Level: "+clientInstance.setLevel(getLevel()));
    }

    public void setLevel(String level){
        set("Level",level);
    }

    public String getLevel(){
        return get("Level");
    }
}
