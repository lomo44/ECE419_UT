package app_kvClient.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVJSONMessage;

public class KVCommandHelp extends KVCommand<KVClient> {
    public KVCommandHelp() {
        super(KVCommandPattern.KVCommandType.HELP);
    }
    @Override
    public KVJSONMessage execute(KVClient clientInstance) {
        clientInstance.printHelp();
        return null;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        // Ignore
    }
}
