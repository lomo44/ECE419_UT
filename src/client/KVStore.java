package client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.net.SocketException;
import java.lang.System;
import java.math.BigInteger;

import common.enums.eKVExtendStatusType;
import common.enums.eKVLogLevel;

import common.networknode.KVStorageNode;

import common.messages.KVMessage;
import common.messages.KVJSONMessage;
import common.communication.KVCommunicationModule;
import logger.KVOut;

import common.metadata.KVMetadataController;

public class KVStore implements KVCommInterface {

    private KVOut kv_out = new KVOut("client");
    private boolean running;
    private String serverAddress;
    private int serverPort;
    private Socket clientSocket;
    private eKVLogLevel outputlevel = eKVLogLevel.DEBUG;
    private eKVLogLevel logLevel = eKVLogLevel.DEBUG;
    private KVCommunicationModule communicationModule;

    // Milestone 2
    private KVMetadataController data_controller;

    /**
     * Initialize KVStore with address and port of KVServer
     * @param address the address of the KVServer
     * @param port the port of the KVServer
     */
    public KVStore(String address, int port) {
        data_controller = new KVMetadataController();
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
        // kv_out.println_info("Connection established.");

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
    public void disconnect() {
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

    public void reconnect(String address, int port) {
        // disconnect
        disconnect();
        // connect
        serverAddress = address;
        serverPort = port;
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return communicationModule.isConnected();
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
        // try sending once (naive)
        KVJSONMessage newmessage = createEmptyMessage();
        newmessage.setValue(value);
        newmessage.setKey(key);
        newmessage.setStatus(KVMessage.StatusType.PUT);
        // set up iterator starting at the next server
        BigInteger hashedKey = data_controller.hash(key);
        BigInteger nextHashedKey = null;
        SortedSet<BigInteger> keys = data_controller.getHashes();
        Iterator<BigInteger> itor = keys.iterator();
        while (itor.hasNext()) {
            nextHashedKey = itor.next();
            if (nextHashedKey.compareTo(hashedKey) > 0) break;
        }
        while (true) {
            try {
                communicationModule.send(newmessage);
                break;
            } catch (SocketException e) {
                // server has been removed, try next server in consistent hash
                // loop around
                if (!itor.hasNext() && !keys.isEmpty())
                    itor = keys.iterator();
                if (itor.hasNext())
                    nextHashedKey = itor.next();
                // loop back to original server, throw exception
                if (keys.isEmpty() || nextHashedKey == hashedKey) throw e;
                // reconnect
                KVStorageNode server = data_controller.getStorageNodeFromHash(nextHashedKey);
                reconnect(server.getHostName(),server.getPortNumber());
                continue;
            } catch (SocketTimeoutException ste) {
                throw ste;
            }
        }
        KVJSONMessage response = communicationModule.receiveMessage();
        // retry if send to wrong server
        while (response.getExtendStatusType() == eKVExtendStatusType.SERVER_NOT_RESPONSIBLE) {
            data_controller.update(response.getMetadata());
            KVStorageNode server = getResponsibleServer(data_controller.hash(key));
            // create new socket connection
            reconnect(server.getHostName(),server.getPortNumber());
            communicationModule.send(newmessage);
            response = communicationModule.receiveMessage();
        }
        kv_out.println_debug("PUT RTT: " + (System.currentTimeMillis()-response.getSendTime()) + "ms.");
        return response;
    }

    /**
     * Find server responsible for hashed key
     * @param hashedKey the hashed key that identifies the value
     * @return KVStorageNode corresponding to server
     */
    public KVStorageNode getResponsibleServer(BigInteger hashedKey) {
        return data_controller.getResponsibleStorageNode(hashedKey);
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
        // try sending once (naive)
        KVJSONMessage newmessage = createEmptyMessage();
        newmessage.setKey(key);
        newmessage.setValue("");
        newmessage.setStatus(KVMessage.StatusType.GET);
        // set up iterator starting at the next server
        BigInteger hashedKey = data_controller.hash(key);
        BigInteger nextHashedKey = null;
        SortedSet<BigInteger> keys = data_controller.getHashes();
        Iterator<BigInteger> itor = keys.iterator();
        while (itor.hasNext()) {
            nextHashedKey = itor.next();
            if (nextHashedKey.compareTo(hashedKey) > 0) break;
        }
        while (true) {
            try {
                communicationModule.send(newmessage);
                break;
            } catch (SocketException e) {
                // server has been removed, try next server in consistent hash
                // loop around
                if (!itor.hasNext() && !itor.isEmpty())
                    itor = keys.iterator();
                if (itor.hasNext())
                    nextHashedKey = itor.next();
                // loop back to original server, throw exception
                if (keys.isEmpty() || nextHashedKey == hashedKey) throw e;
                // reconnect
                KVStorageNode server = data_controller.getStorageNodeFromHash(nextHashedKey);
                reconnect(server.getHostName(),server.getPortNumber());
                continue;
            } catch (SocketTimeoutException ste) {
                throw ste;
            }
        }
        KVJSONMessage response = communicationModule.receiveMessage();
        // retry if send to wrong server
        while (response.getExtendStatusType() == eKVExtendStatusType.SERVER_NOT_RESPONSIBLE) {
            data_controller.update(response.getMetadata());
            KVStorageNode server = getResponsibleServer(data_controller.hash(key));
            // create new socket connection
            reconnect(server.getHostName(),server.getPortNumber());
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
        // try sending once (naive)
        communicationModule.send(outboundmsg);
        KVJSONMessage response = communicationModule.receiveMessage();
        // retry if send to wrong server
        while (response.getExtendStatusType() == eKVExtendStatusType.SERVER_NOT_RESPONSIBLE) {
            data_controller.update(response.getMetadata());
            KVStorageNode server = getResponsibleServer(data_controller.hash(outboundmsg.getKey()));
            // create new socket connection
            reconnect(server.getHostName(),server.getPortNumber());
            communicationModule.send(outboundmsg);
            response = communicationModule.receiveMessage();
        }
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
