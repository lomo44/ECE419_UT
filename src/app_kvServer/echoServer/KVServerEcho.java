package app_kvServer.echoServer;

import app_kvServer.KVServer;
import app_kvServer.KVServerHandler;

public class KVServerEcho extends KVServer {

    public KVServerEcho(int port, int cacheSize, String strategy) {
        super(port, cacheSize, strategy);
    }

    @Override
    public KVServerHandler createServerHandler() {
        return new KVServerEchoHandler(this.getPort(),this);
    }
}