package client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.net.SocketException;
import java.lang.System;

import common.enums.eKVLogLevel;
import common.messages.KVJSONMessage;

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
    private eKVLogLevel outputlevel = eKVLogLevel.DEBUG;
    private eKVLogLevel logLevel = eKVLogLevel.DEBUG;
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
        kv_out.println_debug("KV Store connect");
        clientSocket = new Socket(serverAddress, serverPort);
        communicationModule = new KVCommunicationModule(clientSocket,2000);
        communicationModule.setLogLevel(outputlevel,logLevel);
        setRunning(true);
        setLogLevel(outputlevel,logLevel);
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
        KVJSONMessage response = communicationModule.receiveMessage();
        kv_out.println_debug("PUT RTT: " + (System.currentTimeMillis()-response.getSendTime()) + "ms.");
        return response;
    }

    @Override
    public KVMessage get(String key) throws SocketTimeoutException, SocketException {
        KVJSONMessage newmessage = createEmptyMessage();
        newmessage.setKey(key);
        newmessage.setValue("");
        newmessage.setStatus(KVMessage.StatusType.GET);
        communicationModule.send(newmessage);
        KVJSONMessage response = communicationModule.receiveMessage();
        kv_out.println_debug("GET RTT: " + (System.currentTimeMillis()-response.getSendTime()) + " ms.");
        return response;
    }

    public KVMessage send(KVMessage outboundmsg) throws SocketException, SocketTimeoutException {
        communicationModule.send(outboundmsg);
        KVJSONMessage response = communicationModule.receiveMessage();
        kv_out.println_debug("ECHO RTT: " + (System.currentTimeMillis()-response.getSendTime()) + " ms.");
        return response;
    }

    public KVJSONMessage createEmptyMessage() {
        return communicationModule.getEmptyMessage();
    }

    public void setLogLevel(eKVLogLevel outputlevel, eKVLogLevel logLevel){
        kv_out.changeOutputLevel(outputlevel);
        kv_out.changeLogLevel(logLevel);
        if(communicationModule!=null)
            communicationModule.setLogLevel(outputlevel,logLevel);
        else {
            this.outputlevel = outputlevel;
            this.logLevel = logLevel;
        }
    }
}
