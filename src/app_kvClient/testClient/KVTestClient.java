package app_kvClient.testClient;


import common.communication.KVCommunicationModule;
import common.messages.KVMessage;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Simple client that used for testing
 */
public class KVTestClient {
    private String hostname;
    private int portnumber;
    private KVCommunicationModule communicationModule;
    private boolean initialized;

    /**
     * Constructor for test client
     * @param hostname hostname of the server
     * @param portnumber port of the server
     */
    public KVTestClient(String hostname, int portnumber){
        this.hostname = hostname;
        this.portnumber = portnumber;
        initialized = false;
    }

    /**
     * Init the client
     * @param timeout client side timeout
     * @throws IOException thrown when there is a problem connecting to the client
     */
    public void init(int timeout) throws IOException {
        if(initialized == false){
            Socket newsocket = new Socket(this.hostname,this.portnumber);
            communicationModule = createCommunicationModule(newsocket, timeout);
            initialized = true;
        }
    }

    /**
     * Tear down the client
     * @throws IOException thrown when there is problem closing the communication between server
     */
    public void teardown() throws IOException {
        communicationModule.close();
    }

    /**
     * Create a communication module, can be override to create difference communication module
     * @param socket live socket
     * @param timeout timeout for communication module
     * @return KVCommunicationModule instance
     */
    public KVCommunicationModule createCommunicationModule(Socket socket, int timeout){
        return new KVCommunicationModule(socket,0);
    }


    /**
     * Send a message from client
     * @param msg KVMessage
     * @throws SocketException thrown if the connection is closed
     * @throws SocketTimeoutException thrown if time out
     */
    public void send(KVMessage msg) throws SocketException, SocketTimeoutException {
        communicationModule.send(msg);
    }

    /**
     * Retrieve a message from server
     * @return KVMessage instance send from server
     * @throws SocketException thrown when socket is closed
     * @throws SocketTimeoutException thrown when receiving time out
     */
    public KVMessage get() throws SocketException, SocketTimeoutException {
        return communicationModule.receiveMessage();
    }

    /**
     * Create a new KVMessage
     * @return empty KVMessage
     */
    public KVMessage createKVMessage(){
        return communicationModule.getEmptyMessage();
    }
}
