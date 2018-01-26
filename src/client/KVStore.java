package client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.net.SocketException;

import common.messages.KVJSONMessage;
import org.apache.log4j.Logger;

import common.messages.KVMessage;
import common.communication.KVCommunicationModule;
import logger.KVOut;

public class KVStore implements KVCommInterface {

    private KVOut kv_out = new KVOut();
    private Set<app_kvClient.IKVClient> listeners;
    private boolean running;
    private String serverAddress;
    private int serverPort;
    private Socket clientSocket;
    private KVCommunicationModule communicationModule;

    /**
     * Initialize KVStore with address and port of KVServer
     * @param address the address of the KVServer
     * @param port the port of the KVServer
     */
    public KVStore(String address, int port) {
        serverAddress = address;
        serverPort = port;
        listeners = new HashSet<app_kvClient.IKVClient>();
    }

    //TODO: Handle connection exception
    @Override
    public void connect() throws Exception {
        clientSocket = new Socket(serverAddress, serverPort);
        communicationModule = new KVCommunicationModule(clientSocket,1000);
        setRunning(true);
        kv_out.println_info("Connection established.");

    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void disconnect(){
        setRunning(false);
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            communicationModule = null;
            clientSocket = null;
        }
    }

    @Override
    public KVMessage put(String key, String value) throws SocketException, SocketTimeoutException {
        KVJSONMessage newmessage = createEmptyMessage();
        newmessage.setValue(value);
        newmessage.setKey(key);
        newmessage.setStatus(KVMessage.StatusType.PUT);
        communicationModule.send(newmessage);
        return communicationModule.receiveMessage();
    }

    @Override
    public KVMessage get(String key) throws SocketTimeoutException, SocketException {
        KVJSONMessage newmessage = createEmptyMessage();
        newmessage.setKey(key);
        newmessage.setValue("");
        newmessage.setStatus(KVMessage.StatusType.GET);
        communicationModule.send(newmessage);
        return communicationModule.receiveMessage();
    }

    public KVMessage send(KVMessage outboundmsg) throws SocketException, SocketTimeoutException {
        communicationModule.send(outboundmsg);
        return communicationModule.receiveMessage();
    }

    public KVJSONMessage createEmptyMessage() {
        return communicationModule.getEmptyMessage();
    }
}
