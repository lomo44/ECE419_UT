package app_kvServer;


import java.net.ServerSocket;
import java.net.Socket;

// Incomming connection handler
public class KVServerHandler implements Runnable {
    private int port;
    private ServerSocket serverSocket;
    private KVServer master;

    /**
     * Common thread implementation
     */
    @Override
    public void run() {
        try{
            serverSocket = new ServerSocket(port);
            while(true){
                initiateServerInstance(serverSocket.accept());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Construct a server handler
     * @param port port of the server
     * @param managerServer instance of the management server
     */
    public KVServerHandler(int port, KVServer managerServer){
        this.port = port;
        master = managerServer;
    }

    /**
     * Initiate a new server instance
     * @param newSocket income socket
     */
    public void initiateServerInstance(Socket newSocket){
        master.registerConnection(newSocket);
    }
}
