package app_kvServer;

public class KVServerDaemon implements Runnable{
    KVServer serverInstance;
    public KVServerDaemon(KVServer serverInstance){
        this.serverInstance = serverInstance;
    }
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                break;
            }
        }
        serverInstance.daemonShutdownHandle();
    }
}
