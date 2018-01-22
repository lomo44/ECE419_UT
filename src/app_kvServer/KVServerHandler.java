package app_kvServer;


import common.communication.KVCommunicationModule;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Vector;

// Incomming connection handler
public class KVServerHandler implements Runnable {
    private Vector<Thread> aliveinstancethreads;
    private Vector<KVServerInstance> aliveInstances;
    private int port;
    private ServerSocket serverSocket;
    private KVServer master;
    private boolean isRunning;
    private int listenerTimerout;
    /**
     * Common thread implementation
     */
    @Override
    public void run() {
        try{
            serverSocket = new ServerSocket(port);
            if (this.listenerTimerout > 0) {
                serverSocket.setSoTimeout(this.listenerTimerout);
            }
        }
        catch (SocketException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                serverSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        isRunning = true;
        if(serverSocket != null){
            while(isRunning && !serverSocket.isClosed()){
                try {
                    initiateServerInstance(serverSocket.accept());
                }
                catch (SocketTimeoutException e){
                    System.out.println("Server timeout");
                }
                catch (SocketException e){
                    // Socket close
                    isRunning = false;
                }
                catch (IOException e) {

                }

            }
        }
        try {
            if(serverSocket != null){
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Construct a server handler
     * @param port port of the server
     * @param managerServer instance of the management server
     */
    public KVServerHandler(int port, KVServer managerServer, int listener_timeout){
        this.port = port;
        master = managerServer;
        aliveinstancethreads = new Vector<Thread>();
        aliveInstances = new Vector<KVServerInstance>();
        isRunning = false;
        this.listenerTimerout = listener_timeout;
    }

    /**
     * Create a communication module. Can be overwritten to create a different communication module
     * Default implementation will generatate the default communication module
     * @param socket Socket used for communication module
     * @return KVCommunication module instance
     */
    public KVCommunicationModule createCommunicationModule(Socket socket){
        return new KVCommunicationModule(socket,listenerTimerout);
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
        for (int i = 0; i < this.aliveinstancethreads.size() ; i++) {
            aliveinstancethreads.elementAt(i).join();
        }
    }

    /**
     * Stop the server handler. this function will call the tear down method to try to stop all
     * server instances.
     * @throws InterruptedException thrown when threads cannot be joined
     * @throws IOException thrown when buffer cannot be clo
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
        Thread aliveinstancethread = new Thread(instance);
        aliveinstancethread.start();
        aliveinstancethreads.add(aliveinstancethread);
    }
}
