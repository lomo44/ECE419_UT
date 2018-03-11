package client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.net.SocketException;
import java.lang.System;
import java.math.BigInteger;

import common.networknode.*;

import common.enums.eKVExtendStatusType;
import common.enums.eKVLogLevel;

import common.messages.KVJSONMessage;
import common.KVMessage;
import common.communication.KVCommunicationModule;
import common.metadata.KVMetadataController;
import common.metadata.KVMetadata;
import logger.KVOut;

public class KVStore implements KVCommInterface {

    private KVOut kv_out = new KVOut("client");
    private boolean running;
    private String serverAddress;
    private int serverPort;
    private Socket clientSocket;
    private eKVLogLevel outputlevel = eKVLogLevel.DEBUG;
    private eKVLogLevel logLevel = eKVLogLevel.DEBUG;
    private KVCommunicationModule communicationModule;
    private KVMetadataController metadataController;
    private boolean serverReconnectEnable = true;

    /**
     * Initialize KVStore with address and port of KVServer
     * @param address the address of the KVServer
     * @param port the port of the KVServer
     */
    public KVStore(String address, int port) {
        metadataController = new KVMetadataController();
        serverAddress = address;
        serverPort = port;
    }

    /**
     * Connect the client to server
     * @throws Exception thrown if the connection failed
     */
    @Override
    public void connect() throws Exception {
        kv_out.println_debug("KV Store connect");
        clientSocket = new Socket(serverAddress, serverPort);
        communicationModule = new KVCommunicationModule(clientSocket,"client");
        communicationModule.setLogLevel(outputlevel,logLevel);
        setRunning(true);
        setLogLevel(outputlevel,logLevel);
        kv_out.println_info("Connection established.");

    }

    /**
     * Check if the store interface is running or not
     * @return true if the store interface is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Set the running states of the store interface
     * @param run run state
     */
    public void setRunning(boolean run) {
        running = run;
    }

    /**
     * Disconnect the interfaces
     */
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
    public boolean isConnected() {
        return communicationModule.isConnected();
    }

    /**
     * Disconnect then reconnect
     * @param address the new hostname
     * @param port the new port
     */
    public void reconnect(String address, int port) {
        disconnect();
        serverAddress = address;
        serverPort = port;
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Issues a put command
     * @param key   the key that identifies the given value.
     * @param value the value that is indexed by the given key.
     * @return return message from server
     * @throws SocketException thrown when socket is closed
     */
    @Override
    public KVMessage put(String key, String value) throws SocketException, InterruptedException {
        KVJSONMessage newmessage = createEmptyMessage();
        newmessage.setValue(value);
        newmessage.setKey(key);
        newmessage.setStatus(KVMessage.StatusType.PUT);
        communicationModule.send(newmessage);
        KVJSONMessage response = communicationModule.receiveMessage();
        while(response.getExtendStatusType()==eKVExtendStatusType.SERVER_NOT_RESPONSIBLE && serverReconnectEnable){
            updateMetadata(response);
            switchServer(key);
            communicationModule.send(newmessage);
            response = communicationModule.receiveMessage();
        }
        kv_out.println_debug("PUT RTT: " + (System.currentTimeMillis()-response.getSendTime()) + "ms.");
        return response;
    }

    /**
     * Updates metadata with version provided by server
     * @param msg response from server containing metadata
     */
    public void updateMetadata(KVJSONMessage msg) {
        KVMetadata metadata = KVMetadata.fromKVJSONMessage(msg);
        metadataController.update(metadata);
    }

    /**
     * Switches server connection according to updated metadata
     * @param key key to be hashed
     */
    public void switchServer(String key) {
        BigInteger hashedKey = metadataController.hash(key);
        KVStorageNode newServer = getResponsibleServer(hashedKey);
        reconnect(newServer.getHostName(), newServer.getPortNumber());
    }

    /**
     * Returns server responsible for hashed key
     * @parm hashedKey the key that identifies the server
     * @return KVStorageNode server
     */
    public KVStorageNode getResponsibleServer(BigInteger hash) {
        return metadataController.getResponsibleStorageNode(hash);
    }

    /**
     * Issues a get command
     * @param key the key that identifies the value.
     * @return return message from server
     * @throws SocketException thrown when socket is closed
     */
    @Override
    public KVMessage get(String key) throws SocketException, InterruptedException {
        KVJSONMessage newmessage = createEmptyMessage();
        newmessage.setKey(key);
        newmessage.setValue("");
        newmessage.setStatus(KVMessage.StatusType.GET);
        communicationModule.send(newmessage);
        KVJSONMessage response = communicationModule.receiveMessage();
        while (response.getExtendStatusType() == eKVExtendStatusType.SERVER_NOT_RESPONSIBLE && serverReconnectEnable){
            updateMetadata(response);
            switchServer(key);
            communicationModule.send(newmessage);
            response = communicationModule.receiveMessage();
        }
        kv_out.println_debug("GET RTT: " + (System.currentTimeMillis()-response.getSendTime()) + " ms.");
        return response;
    }

    /**
     * Generic message for sending and receive message
     * @param outboundmsg outbound message that need to send
     * @return respond from server
     * @throws SocketException thrown if socket is closed
     */
    public KVMessage send(KVMessage outboundmsg) throws SocketException, InterruptedException {
        communicationModule.send(outboundmsg);
        KVJSONMessage response = communicationModule.receiveMessage();
        kv_out.println_debug("ECHO RTT: " + (System.currentTimeMillis()-response.getSendTime()) + " ms.");
        return response;
    }

    /**
     * Create an empty message
     * @return KVJSONMessage
     */
    public static KVJSONMessage createEmptyMessage() {
        return KVCommunicationModule.getEmptyMessage();
    }

    /**
     * Change the log level of the logger
     * @param outputlevel
     * @param logLevel
     */
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

    public void setServerReconnectEnable(boolean serverReconnectEnable) {
        this.serverReconnectEnable = serverReconnectEnable;
    }
}