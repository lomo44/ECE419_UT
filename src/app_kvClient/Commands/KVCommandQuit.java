package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;

public class KVCommandQuit extends KVCommand {
    public KVCommandQuit() {
        super(KVCommandPattern.KVCommandType.QUIT);
    }

    @Override
    public void execute(KVClient clientInstance) {
        clientInstance.stop();
    }
}
