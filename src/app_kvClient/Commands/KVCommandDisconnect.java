package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;

public class KVCommandDisconnect extends KVCommand {
    public KVCommandDisconnect() {
        super(KVCommandPattern.KVCommandType.DISCONNECT);
    }

    @Override
    public void execute(KVClient clientInstance) {
        clientInstance.disconnect();
    }
}
