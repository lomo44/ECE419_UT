package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.messages.KVJSONMessage;

public class KVCommandStart extends KVCommand<ECSClient> {
    public KVCommandStart() { super(KVCommandPattern.KVCommandType.START); }

    @Override
    public KVJSONMessage execute(ECSClient clientInstance) {
        // TODO
        return new KVJSONMessage();
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        // TODO
        return;
    }
}
