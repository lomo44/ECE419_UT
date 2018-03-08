package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.messages.KVJSONMessage;

public class KVCommandShutdown extends KVCommand<ECSClient> {
    public KVCommandShutdown() { super(KVCommandPattern.KVCommandType.SHUT_DOWN); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        // TODO
        return KVJSONMessage;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        // TODO
        return;
    }
}
