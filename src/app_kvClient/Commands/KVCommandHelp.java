package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;

public class KVCommandHelp extends KVCommand {
    public KVCommandHelp() {
        super(KVCommandPattern.KVCommandType.HELP);
    }
    @Override
    public void execute(KVClient clientInstance) {
        clientInstance.printHelp();
    }
}
