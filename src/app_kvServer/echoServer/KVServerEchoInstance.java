package app_kvServer.echoServer;

import app_kvServer.IKVServer;
import app_kvServer.KVServerInstance;
import common.communication.KVCommunicationModule;
import common.messages.KVMessage;

public class KVServerEchoInstance extends KVServerInstance {
    public KVServerEchoInstance(KVCommunicationModule communicationModule, IKVServer server) {
        super(communicationModule, server);
    }
    @Override
    public KVMessage handleMessage(KVMessage in_message){
        return in_message;
    }
}
