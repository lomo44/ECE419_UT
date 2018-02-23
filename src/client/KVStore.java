package client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.net.SocketException;
import java.lang.System;
import java.math.BigInteger;

import ecs.ECSNode;

import common.enums.eKVExtendStatusType;
import common.enums.eKVLogLevel;

import common.messages.KVJSONMessage;
import common.messages.KVMessage;
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
    private KVMetadataController metadata_controller;

    /**
     * Initialize KVStore with address and port of KVServer
     * @param address the address of the KVServer
     * @param port the port of the KVServer
     */
    public KVStore(String address, int port) {
        metadata_controller = new KVMetadataController();
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
        communicationModule = new KVCommunicationModule(clientSocket,500,"client");
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
     * @throws SocketTimeoutException thrown is read timeout
     */
    @Override
    public KVMessage put(String key, String value) throws SocketException, SocketTimeoutException {
        KVJSONMessage newmessage = createEmptyMessage();
        newmessage.setValue(value);
        newmessage.setKey(key);
        newmessage.setStatus(KVMessage.StatusType.PUT);
        BigInteger hashedKey;
        SortedSet<BigInteger> hashedKeys = metadata_controller.getHashes();
        Iterator<BigInteger> itor = null;
        if (hashedKeys != null && !hashedKeys.isEmpty())
            itor = hashedKeys.iterator();
        while (true) {
            try {
                communicationModule.send(newmessage);
                break;
            } catch (SocketException e) {
                if (itor == null || !itor.hasNext()) {
                    System.out.println("Failed to send message.");
                    throw e;
                }
                hashedKey = itor.next();
                ECSNode server = metadata_controller.getStorageNodeFromHash(hashedKey);
                reconnect(server.getNodeHost(),server.getNodePort());
                continue;
            } catch (SocketTimeoutException ste) {
                System.out.println("Socket timeout.");
                throw ste;
            }
        }
        KVJSONMessage response = communicationModule.receiveMessage();
        while (response.getExtendStatusType() == eKVExtendStatusType.SERVER_NOT_RESPONSIBLE) {
            KVJSONMessage metadata_update = createEmptyMessage();
            metadata_update.setExtendStatus(eKVExtendStatusType.METADATA_UPDATE);
            communicationModule.send(metadata_update);
            response = communicationModule.receiveMessage();
            System.out.println("Receive metadata update.");
        }
        if (response.getExtendStatusType() == eKVExtendStatusType.METADATA_UPDATE) {
            KVMetadata metadata = KVMetadata.fromKVJSONMessage(response);
            metadata_controller.update(metadata);
            hashedKey = metadata_controller.hash(key);
            ECSNode server = getResponsibleServer(hashedKey);
            reconnect(server.getNodeHost(), server.getNodePort());
            communicationModule.send(newmessage);
            response = communicationModule.receiveMessage();
            System.out.println("Receive put success.");
        }
        kv_out.println_debug("PUT RTT: " + (System.currentTimeMillis()-response.getSendTime()) + "ms.");
        return response;
    }

    /**
     * Returns server responsible for hashed key
     * @parm hashedKey the key that identifies the server
     * @return KVStorageNode server
     */
    public ECSNode getResponsibleServer(BigInteger hash) {
        return metadata_controller.getResponsibleStorageNode(hash);
    }

    /**
     * Issues a get command
     * @param key the key that identifies the value.
     * @return return message from server
     * @throws SocketTimeoutException thrown when socket read timeout
     * @throws SocketException thrown when socket is closed
     */
    @Override
    public KVMessage get(String key) throws SocketTimeoutException, SocketException {
        KVJSONMessage newmessage = createEmptyMessage();
        newmessage.setKey(key);
        newmessage.setValue("");
        newmessage.setStatus(KVMessage.StatusType.GET);
        BigInteger hashedKey;
        SortedSet<BigInteger> hashedKeys = metadata_controller.getHashes();
        Iterator<BigInteger> itor = null;
        if (hashedKeys != null && !hashedKeys.isEmpty())
            itor = hashedKeys.iterator();
        while (true) {
            try {
                communicationModule.send(newmessage);
                break;
            } catch (SocketException e) {
                if (itor == null || !itor.hasNext())
                    throw e;
                hashedKey = itor.next();
                ECSNode server = metadata_controller.getStorageNodeFromHash(hashedKey);
                reconnect(server.getNodeHost(),server.getNodePort());
                continue;
            } catch (SocketTimeoutException ste) {
                throw ste;
            }
        }
        KVJSONMessage response = communicationModule.receiveMessage();
        while (response.getExtendStatusType() == eKVExtendStatusType.SERVER_NOT_RESPONSIBLE) {
            KVJSONMessage metadata_update = createEmptyMessage();
            metadata_update.setExtendStatus(eKVExtendStatusType.METADATA_UPDATE);
            communicationModule.send(metadata_update);
            response = communicationModule.receiveMessage();
        }
        if (response.getExtendStatusType() == eKVExtendStatusType.METADATA_UPDATE) {
            KVMetadata metadata = KVMetadata.fromKVJSONMessage(response);
            metadata_controller.update(metadata);
            hashedKey = metadata_controller.hash(key);
            ECSNode server = getResponsibleServer(hashedKey);
            reconnect(server.getNodeHost(), server.getNodePort());
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
     * @throws SocketTimeoutException thrown is socket is timeout
     */
    public KVMessage send(KVMessage outboundmsg) throws SocketException, SocketTimeoutException {
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
}