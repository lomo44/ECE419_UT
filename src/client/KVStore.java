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
import database.storage.KVStorage;
import logger.KVOut;

public class KVStore implements KVCommInterface {

    private KVOut kv_out = new KVOut("client");
    private boolean running;
    private String serverAddress;
    private int serverPort;
    private eKVLogLevel outputlevel = eKVLogLevel.DEBUG;
    private eKVLogLevel logLevel = eKVLogLevel.DEBUG;
    private KVMetadataController metadataController;
    private HashMap<KVNetworkNode, KVCommunicationModule> connectionMap = new HashMap<>();
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
        KVNetworkNode newNetworkNode = new KVNetworkNode(serverAddress,serverPort,"default");
        KVCommunicationModule newModule = newNetworkNode.createCommunicationModule();
        newModule.setLogLevel(outputlevel,logLevel);
        setRunning(true);
        setLogLevel(outputlevel,logLevel);
        kv_out.println_info("Connection established.");
        connectionMap.put(newNetworkNode,newModule);
        metadataController.update(new KVMetadata());
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
        for (KVNetworkNode node: connectionMap.keySet()
             ) {
            try {
                connectionMap.get(node).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connectionMap.clear();
    }

    @Override
    public boolean isConnected() {
        return isRunning();
    }

    /**
     * Issues a put command
     * @param key   the key that identifies the given value.
     * @param value the value that is indexed by the given key.
     * @return return message from server
     * @throws SocketException thrown when socket is closed
     */
    @Override
    public KVMessage put(String key, String value){
        KVJSONMessage response = new KVJSONMessage();
        if(isRunning()){
            KVJSONMessage newmessage = createEmptyMessage();
            newmessage.setValue(value);
            newmessage.setKey(key);
            newmessage.setStatus(KVMessage.StatusType.PUT);
            boolean sendSuccess = false;
            while(sendSuccess!=true){
                KVCommunicationModule module = getResponsibleCommunicationModule(key);
                try{
                    module.send(newmessage);
                    response = module.receiveMessage();
                } catch (SocketException e) {
                    connectionMap.values().remove(module);
                    if(connectionMap.size()==0){
                        running = false;
                        break;
                    }
                }
                if(response.getExtendStatusType()!=eKVExtendStatusType.SERVER_NOT_RESPONSIBLE){
                    sendSuccess = true;
                    kv_out.println_debug("PUT RTT: " + (System.currentTimeMillis()-response.getSendTime()) + "ms.");
                }
                else{
                    if(!this.serverReconnectEnable){
                        sendSuccess = true;
                    }
                    else{
                        updateMetadata(response);
                    }
                }
            }
            if(sendSuccess == false){
                response.setValue("");
                response.setStatus(KVMessage.StatusType.PUT_ERROR);
                kv_out.println_error("Message send failed, no available server");
                running = false;
            }
        }
        else{
            response.setValue("");
            response.setExtendStatus(eKVExtendStatusType.NO_RESPONSE);
            kv_out.println_error("Message send failed, KVStore not running");
        }
        return response;
    }

    /**
     * Updates metadata with version provided by server
     * @param msg response from server containing metadata
     */
    public void updateMetadata(KVJSONMessage msg) {
        KVMetadata metadata = KVMetadata.fromKVJSONMessage(msg);
        metadataController.clearStorageNodes();
        metadataController.update(metadata);
    }

    /**
     * Returns server responsible for hashed key
     * @parm hashedKey the key that identifies the server
     * @return KVStorageNode server
     */
    public KVCommunicationModule getResponsibleCommunicationModule(String key){
        KVNetworkNode node = metadataController.getResponsibleStorageNode(metadataController.hash(key));
        if(node==null){
            for (KVNetworkNode remainNode: connectionMap.keySet()
                 ) {
                return connectionMap.get(remainNode);
            }
        }
        else{
            if(!connectionMap.containsKey(node)){
                try {
                    connectionMap.put(node,node.createCommunicationModule());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return connectionMap.get(node);
        }
        return null;
    }

    /**
     * Issues a get command
     * @param key the key that identifies the value.
     * @return return message from server
     * @throws SocketException thrown when socket is closed
     */
    @Override
    public KVMessage get(String key){
        KVJSONMessage response = new KVJSONMessage();
        if(isRunning()){
            KVJSONMessage newmessage = createEmptyMessage();
            newmessage.setKey(key);
            newmessage.setValue("");
            newmessage.setStatus(KVMessage.StatusType.GET);
            boolean getSuccess = false;
            while(getSuccess!=true){
                KVCommunicationModule module = getResponsibleCommunicationModule(key);
                try{
                    module.send(newmessage);
                    response = module.receiveMessage();
                }
                catch (SocketException e){
                    connectionMap.values().remove(module);
                    if(connectionMap.size()==0){
                        running = false;
                        break;
                    }
                }
                if(response.getExtendStatusType()!=eKVExtendStatusType.SERVER_NOT_RESPONSIBLE){
                    getSuccess = true;
                    kv_out.println_debug("GET RTT: " + (System.currentTimeMillis()-response.getSendTime()) + " ms.");
                }
                else{
                    if(!serverReconnectEnable){
                        getSuccess = true;
                    }
                    else{
                        updateMetadata(response);
                    }
                }
            }
            if(getSuccess==false){
                response.setKey(key);
                response.setValue("");
                response.setStatus(KVMessage.StatusType.GET_ERROR);
                kv_out.println_error("Message send failed, no available server");
            }
        }
        else{
            response.setKey(key);
            response.setValue("");
            response.setExtendStatus(eKVExtendStatusType.NO_RESPONSE);
            kv_out.println_error("Message send failed, KVStore not running");
        }
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
        for (KVNetworkNode node: connectionMap.keySet()
             ) {
            connectionMap.get(node).setLogLevel(outputlevel,logLevel);
        }
    }

    public void setServerReconnectEnable(boolean serverReconnectEnable) {
        this.serverReconnectEnable = serverReconnectEnable;
    }
}