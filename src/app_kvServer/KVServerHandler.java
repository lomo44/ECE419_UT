package app_kvServer;


import common.communication.KVCommunicationModule;
import common.enums.eKVLogLevel;
import logger.KVOut;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

// Incomming connection handler
public class KVServerHandler implements Runnable {
    private Vector<Thread> aliveinstancethreads;
    private Vector<KVServerInstance> aliveInstances;
    private int port;
    private ServerSocket serverSocket;
    private KVServer master;
    private KVOut kv_out = new KVOut("server");
    private boolean isRunning;
    /**
     * Common thread implementation
     */
    @Override
    public void run() {
        kv_out.println_info("Initialize server.");
        try{
            serverSocket = new ServerSocket(port);
            kv_out.println_info("Server listening on port "+port);
        }
        catch (SocketException e){
            // e.printStackTrace();
            kv_out.println_error("Cannot open server socket on port "+port);
        } catch (IOException e) {
            // e.printStackTrace();
            kv_out.println_error("Cannot open server socket "+port);
            try {
                serverSocket.close();
            } catch (IOException e1) {
                // e1.printStackTrace();
                kv_out.println_error("Unable to close server socket on port "+port);
            }
        }
        isRunning = true;
        if(serverSocket != null){
            while(isRunning && !serverSocket.isClosed()){
                try {
                    Socket client = serverSocket.accept();
                    initiateServerInstance(client);
                    kv_out.println_info("Server connected to "+client.getInetAddress().getHostName()+" on port "+client.getPort());
                }
                catch (SocketException e){
                    // Socket close
                    isRunning = false;
                    kv_out.println_error("Unable to establish connection.");
                }
                catch (IOException e) {

                }

            }
            kv_out.println_info("Server stopped.");
        }
        try {
            tearDown();
            if(serverSocket != null){
                serverSocket.close();
                serverSocket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            kv_out.println_error("Unable to close server socket on port "+port);
        }
        kv_out.println_debug("Handler exit");
    }

    /**
     * Construct a server handler
     * @param port port of the server
     * @param managerServer instance of the management server
     */
    public KVServerHandler(int port, KVServer managerServer){
        this.port = port;
        master = managerServer;
        aliveinstancethreads = new Vector<Thread>();
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
        KVCommunicationModule module = new KVCommunicationModule(socket,"server");
        module.setLogLevel(kv_out.getOutputLevel(),kv_out.getLogLevel());
        return module;
    }

    /**
     * Create a server instance. Default implementation will generate the normal M1 server instance
     * @param com KVCommunicationmodule,
     * @param master Singleton master server interface
     * @return a new server instance
     */
    public KVServerInstance createServerInstance(KVCommunicationModule com, IKVServer master){
        KVServerInstance newInstance =  new KVServerInstance(com,master);
        newInstance.changeLogLevel(kv_out.getOutputLevel(),kv_out.getLogLevel());
        return newInstance;
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
            kv_out.println_debug("Try to join: "+i);
            aliveinstancethreads.elementAt(i).join();
        }
        kv_out.println_debug("Tear down complete");
    }

    /**
     * Stop the server handler. this function will call the tear down method to try to stop all
     * server instances.
     * @throws InterruptedException thrown when threads cannot be joined
     * @throws IOException thrown when buffer cannot be clo
     */
    public void stop() throws IOException {
        kv_out.println_debug("Try to stop the handler");
        isRunning = false;
        serverSocket.close();
    }

    /**
     * Check if the server handler is running
     * @return
     */
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

    /**
     * Get the port that the handler is running
     * @return
     */
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    /**
     * Change the log and output level of the logger for this handler
     * @param outputlevel output level
     * @param logLevel log level
     */
    public void setLogLevel(eKVLogLevel outputlevel, eKVLogLevel logLevel){
        kv_out.changeOutputLevel(outputlevel);
        kv_out.changeLogLevel(logLevel);
        for (KVServerInstance instance: aliveInstances
             ) {
            instance.changeLogLevel(outputlevel,logLevel);
        }
    }
}
