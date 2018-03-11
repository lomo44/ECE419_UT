package app_kvServer;

public class KVServerDaemon implements Runnable{
    KVServer serverInstance;
    public KVServerDaemon(KVServer serverInstance){
        this.serverInstance = serverInstance;
    }
    @Override
    public void run() {
        System.out.println("Server Daemon started");
        while(true){
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                break;
            }
        }
        serverInstance.daemonShutdownHandle();
        System.out.println("Server exit. Daemon finished");
    }
}
