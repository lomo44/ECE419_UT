package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVMessage;

public class KVCommandHelp extends KVCommand {
    public KVCommandHelp() {
        super(KVCommandPattern.KVCommandType.HELP);
    }
    @Override
    public KVMessage execute(KVClient clientInstance) {
        clientInstance.printHelp();
        return null;
    }

    @Override
    public void handleResponse(KVMessage response) {
        // Ignore
    }
}