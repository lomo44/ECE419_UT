package app_kvClient.testClient;


import common.communication.KVCommunicationModule;
import common.messages.KVMessage;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class KVTestClient {
    private String hostname;
    private int portnumber;
    private KVCommunicationModule communicationModule;
    private boolean initialized;
    public KVTestClient(String hostname, int portnumber){
        this.hostname = hostname;
        this.portnumber = portnumber;
        initialized = false;
    }

    public void init(int timeout) throws IOException {
        if(initialized == false){
            Socket newsocket = new Socket(this.hostname,this.portnumber);
            communicationModule = createCommunicationModule(newsocket, timeout);
            initialized = true;
        }
    }

    public void teardown() throws IOException {
        communicationModule.close();
    }
    public KVCommunicationModule createCommunicationModule(Socket socket, int timeout){
        return new KVCommunicationModule(socket,0);
    }

    public void send(KVMessage msg) throws SocketException {
        communicationModule.send(msg);
    }
    public KVMessage get() throws SocketException {
        return communicationModule.receiveMessage();
    }
    public KVMessage createKVMessage(){
        return KVCommunicationModule.getEmptyMessage();
    }
}
