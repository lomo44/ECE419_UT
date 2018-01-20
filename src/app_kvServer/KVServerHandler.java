package app_kvServer;


import common.communication.KVCommunicationModule;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

// Incomming connection handler
public class KVServerHandler implements Runnable {
    private Vector<Thread> aliveinstancethread;
    private Vector<KVServerInstance> aliveInstances;
    private int port;
    private ServerSocket serverSocket;
    private KVServer master;
    private boolean isRunning;

    /**
     * Common thread implementation
     */
    @Override
    public void run() {
        try{
            serverSocket = new ServerSocket(port);
            isRunning = true;
            while(isRunning){
                initiateServerInstance(serverSocket.accept());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        aliveinstancethread = new Vector<Thread>();
        aliveInstances = new Vector<KVServerInstance>();
        isRunning = false;
    }

    /**
     * Create a communication module. Can be overwritten to create a different communication module
     * Default implementation will generatate the default communication module
     * @param socket Socket used for communication module
     * @return KVCommunication module instance
     */
    public KVCommunicationModule createCommunicationModule(Socket socket){
        return new KVCommunicationModule(socket,0);
    }

    /**
     * Create a server instance. Default implementation will generate the normal M1 server instance
     * @param com KVCommunicationmodule,
     * @param master Singleton master server interface
     * @return a new server instance
     */
    public KVServerInstance createServerInstance(KVCommunicationModule com, IKVServer master){
        return new KVServerInstance(com,master);
    }

    /**
     * Tear down the server handler. This function wil try to stop all of its server instances
     * @throws InterruptedException thrown when threads cannot be joined
     * @throws IOException thrown when individual instances buffer cannot be closed
     */
    protected void tearDown() throws InterruptedException, IOException {
        for (int i = 0; i < this.aliveInstances.size(); i++) {
            aliveInstances.elementAt(i).stop();
        }
        for (int i = 0; i < this.aliveinstancethread.size() ; i++) {
            aliveinstancethread.elementAt(i).join();
        }
    }

    /**
     * Stop the server handler. this function will call the tear down method to try to stop all
     * server instances.
     * @throws InterruptedException thrown when threads cannot be joined
     * @throws IOException thrown when buffer cannot be closed
     */
    public void stop() throws InterruptedException, IOException {
        isRunning = false;
        tearDown();
    }
    public boolean isRunning(){
        return isRunning;
    }
    /**
     * Initiate a new server instance
     * @param newSocket income socket
     */
    private void initiateServerInstance(Socket newSocket){
        KVCommunicationModule com = createCommunicationModule(newSocket);
        KVServerInstance instance = createServerInstance(com, master);
        aliveInstances.add(instance);
        aliveinstancethread.add(new Thread(instance));
    }
}
