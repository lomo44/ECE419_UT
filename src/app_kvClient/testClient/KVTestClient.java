package app_kvClient.testClient;


import common.communication.KVCommunicationModule;
import common.messages.KVMessage;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

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
     * @throws IOException thrown when there is a problem connecting to the client
     */
    public void init() throws IOException {
        if(initialized == false){
            Socket newsocket = new Socket(this.hostname,this.portnumber);
            communicationModule = createCommunicationModule(newsocket);
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
     * @return KVCommunicationModule instance
     */
    public KVCommunicationModule createCommunicationModule(Socket socket){
        return new KVCommunicationModule(socket,"client");
    }


    /**
     * Send a message from client
     * @param msg KVMessage
     * @throws SocketException thrown if the connection is closed
     */
    public void send(KVMessage msg) throws SocketException {
        communicationModule.send(msg);
    }

    /**
     * Retrieve a message from server
     * @return KVMessage instance send from server
     * @throws SocketException thrown when socket is closed
     */
    public KVMessage get() throws SocketException, InterruptedException {
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
