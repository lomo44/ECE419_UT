package app_kvServer.echoServer;

import app_kvServer.IKVServer;
import app_kvServer.KVServer;
import app_kvServer.KVServerHandler;
import app_kvServer.KVServerInstance;
import common.communication.KVCommunicationModule;

public class KVServerEchoHandler extends KVServerHandler {
    /**
     * Construct a server handler
     *
     * @param port          port of the server
     * @param managerServer instance of the management server
     */

    public KVServerEchoHandler(int port, KVServer managerServer) {
        super(port, managerServer, 500);
    }
    @Override
    public KVServerInstance createServerInstance(KVCommunicationModule com, IKVServer master){
        return new KVServerEchoInstance(com,master);
    }
}
