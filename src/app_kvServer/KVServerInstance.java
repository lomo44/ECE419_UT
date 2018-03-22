package app_kvServer;

import common.communication.KVCommunicationModule;
import common.enums.eKVExtendStatusType;
import common.enums.eKVLogLevel;
import common.messages.KVClusterOperationMessage;
import common.messages.KVJSONMessage;
import common.messages.KVMigrationMessage;
import common.messages.KVPrimaryDeclarationMessage;
import logger.KVOut;

import static common.KVMessage.StatusType.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class KVServerInstance implements Runnable {

    private KVCommunicationModule communicationModule;
    private KVServer serverinstance;
    private KVOut kv_out = new KVOut("server");
    private boolean isRunning;
    private static final String DELETE_IDENTIFIER = "null";
    private Pattern whitespacechecker = Pattern.compile("\\s");

    public KVServerInstance(KVCommunicationModule communicationModule, KVServer server){
        this.communicationModule = communicationModule;
        serverinstance = server;
        isRunning = false;
    }

    /**
     * Common implementation for thread
     */
    @Override
    public void run() {
        kv_out.println_debug("New server instance created");
        isRunning = true;
        while(communicationModule.isConnected() && isRunning){
            try {
                KVJSONMessage in_msg = communicationModule.receive();
                KVJSONMessage response = handleMessage(in_msg);
                if(response!=null){
                    communicationModule.send(response);
                }
            }
            catch (SocketException e){
                isRunning = false;
            }
        }
        try {
            communicationModule.close();
        } catch (IOException e) {

        }
        kv_out.println_debug("Instance exit");

    }

    /**
     * Stop the current server instance. This function will try to stop the thread stub and
     * close the communication module
     * @throws IOException thrown when communication module fail to close
     */
    public void stop() throws IOException {
        isRunning = false;
        communicationModule.close();
    }

    /**
     * Handle a input message and generate a output message. Can be override in the
     * derived class to handle message differently
     * @param in_message inbound message
     * @return KVMessage outbound message
     */
    public KVJSONMessage handleMessage(KVJSONMessage in_message) {
        String out = String.format("Received inbound message, key: %s, value: %s,Operator: %d",
                in_message.getKey(),in_message.getValue(),in_message.getExtendStatusType().getValue());
        kv_out.println_debug(out);
        System.out.printf("Server: %s serving %s\n",this.serverinstance.getUID(),new String(in_message.toBytes()));
        eKVExtendStatusType statusType = in_message.getExtendStatusType();
        KVJSONMessage retMessage = communicationModule.getEmptyMessage();
        switch (statusType){
            case GET:{
                if(serverinstance.isStopped()){
                    retMessage = handleServerStopped(in_message);
                }
                else{
                    serverinstance.lockRead();
                    retMessage = handleGet(in_message);
                    serverinstance.unlockRead();
                }
                break;
            }
            case PUT:{
                if(serverinstance.isStopped()){
                    retMessage = handleServerStopped(in_message);
                }
                else{
                    serverinstance.lockRead();
                    retMessage = handlePut(in_message);
                    handleDepartingPrimaryUpdate(in_message);
                    serverinstance.unlockRead();
                }
                break;
            }
            case ECHO:{
                retMessage = in_message;
                break;
            }
            case MIGRATION_DATA:{
                serverinstance.lockWrite();
                retMessage = handleMigration(in_message);
                serverinstance.unlockWrite();
                break;
            }
            case SERVER_STOP:{
                retMessage = handleStop(in_message);
                break;
            }
            case SERVER_START:{
                retMessage = handleStart(in_message);
                break;
            }
            case SERVER_SHUTDOWN:{
                retMessage = handleShutdown(in_message);
                break;
            }
            case CLEAR_STORAGE:{
                serverinstance.lockWrite();
                retMessage = handleClearStorage(in_message);
                serverinstance.unlockWrite();
                break;
            }
            case PRIMARY_UPDATE:{
                retMessage = handleIncomingPrimaryUpdate(in_message);
                break;
            }
            case PRIMARY_DECLARE:{
                retMessage = handlePrimaryDeclaration(in_message);
                break;
            }
            case CLUSTER_OPERATION:{
                retMessage = handleClusterOperation(in_message);
            }
            default:{
                retMessage.setExtendStatus(eKVExtendStatusType.UNKNOWN_ERROR);
                break;
            }
        }
        return retMessage;
    }

    /**
     * Change the output and log level
     * @param outputlevel
     * @param logLevel
     */
    public void changeLogLevel(eKVLogLevel outputlevel, eKVLogLevel logLevel){
        kv_out.changeLogLevel(logLevel);
        kv_out.changeOutputLevel(outputlevel);
        communicationModule.setLogLevel(outputlevel,logLevel);
    }

    private KVJSONMessage handleDelete(KVJSONMessage msg){
        KVJSONMessage emptyMessage = communicationModule.getEmptyMessage();
        if(isKeyValid(msg.getKey())){
            if(isKeyResponsible(msg.getKey(),true)){
                try {
                    serverinstance.getKV(msg.getKey());
                } catch (Exception e) {
                    emptyMessage.setStatus(DELETE_ERROR);
                    return emptyMessage;
                }
                try {
                    serverinstance.putKV(msg.getKey(),msg.getValue());
                    emptyMessage.setStatus(DELETE_SUCCESS);
                } catch (Exception e) {
                    emptyMessage.setStatus(DELETE_ERROR);
                }
            }
            else{
                return handleIrresponsibleRequest();
            }
        }
        else{
            emptyMessage.setStatus(DELETE_ERROR);
        }
        return emptyMessage;
    }
    private KVJSONMessage handlePut(KVJSONMessage msg){
        if(isValidDeleteIdentifier(msg.getValue())){
            return handleDelete(msg);
        }
        else{
            KVJSONMessage response = communicationModule.getEmptyMessage();
            if(isKeyValid(msg.getKey())){
                if(isKeyResponsible(msg.getKey(),true)){
                    try {
                        serverinstance.getKV(msg.getKey());
                        response.setStatus(PUT_UPDATE);
                        response.setKey(msg.getKey());
                        response.setValue(msg.getValue());
                    } catch (Exception e) {
                        // Key doesn't exist, new entry
                        response.setStatus(PUT_SUCCESS);
                    }
                    try {
                        serverinstance.putKV(msg.getKey(),msg.getValue());
                    } catch (Exception e1) {
                        kv_out.println_error(String.format("Key $s is not in range of this server",msg.getKey()));
                        response.setStatus(SERVER_NOT_RESPONSIBLE);
                    }
                }
                else{
                    return handleIrresponsibleRequest();
                }
            }
            else {
                response.setStatus(PUT_ERROR);
            }
            return response;
        }
    }
    private KVJSONMessage handleGet(KVJSONMessage msg){
        KVJSONMessage response = communicationModule.getEmptyMessage();
        if(isKeyValid(msg.getKey())){
            if(isKeyResponsible(msg.getKey(),false)){
                try {
                    String ret = serverinstance.getKV(msg.getKey());
                    response.setKey(msg.getKey());
                    response.setValue(ret);
                    response.setStatus(GET_SUCCESS);
                } catch (Exception e) {
                    System.out.printf("Server %s, cannot find %s \n",this.serverinstance.getUID(),msg.getKey());
                    response.setStatus(GET_ERROR);
                }
            }
            else{
                return handleIrresponsibleRequest();
            }
        }
        else{
            response.setStatus(GET_ERROR);
        }
        return response;
    }
    private KVJSONMessage handleMigration(KVJSONMessage msg){
        //System.out.println("Migration Received, processing start.");
        KVJSONMessage ret = communicationModule.getEmptyMessage();
        if(serverinstance.isStopped()){
            ret.setExtendStatus(eKVExtendStatusType.MIGRATION_INCOMPLETE);
        }
        else{
            ret.setExtendStatus(eKVExtendStatusType.MIGRATION_COMPLETE);
            // Migration process started
            KVMigrationMessage migrationMessage = KVMigrationMessage.fromKVJSONMessage(msg);
            HashMap<String, String> entries = migrationMessage.getEntries();
            for(String key: entries.keySet()){
                try {
                    serverinstance.putKV(key,entries.get(key));
                } catch (Exception e) {
                    kv_out.println_fatal("Incorrect migration data. Key is not in range");
                    ret.setExtendStatus(eKVExtendStatusType.MIGRATION_INCOMPLETE);
                }
            }
        }
        //System.out.println("Migration ends.");
        return ret;
    }
    private KVJSONMessage handleStop(KVJSONMessage msg){
        KVJSONMessage ret = new KVJSONMessage();
        serverinstance.stop();
        ret.setExtendStatus(eKVExtendStatusType.STOP_SUCCESS);
        return ret;
    }
    private KVJSONMessage handleStart(KVJSONMessage msg){
        KVJSONMessage ret = new KVJSONMessage();
        serverinstance.start();
        ret.setExtendStatus(eKVExtendStatusType.START_SUCCESS);
        return ret;
    }
    private KVJSONMessage handleShutdown(KVJSONMessage msg){
        KVJSONMessage ret = new KVJSONMessage();
        serverinstance.closeASync();
        ret.setExtendStatus(eKVExtendStatusType.STOP_SUCCESS);
        return ret;
    }
    private KVJSONMessage handleClearStorage(KVJSONMessage msg){
        KVJSONMessage ret = new KVJSONMessage();
        serverinstance.clearStorage();
        ret.setExtendStatus(eKVExtendStatusType.CLEAR_SUCCESS);
        return ret;
    }
    private KVJSONMessage handleServerStopped(KVJSONMessage msg){
        KVJSONMessage ret = communicationModule.getEmptyMessage();
        ret.setStatus(SERVER_STOPPED);
        return ret;
    }
    private KVJSONMessage handleIrresponsibleRequest(){
        KVJSONMessage ret =  serverinstance.getCurrentMetadata().toKVJSONMessage();
        serverinstance.getCurrentMetadata().print();
        ret.setStatus(SERVER_NOT_RESPONSIBLE);
        return ret;
    }
    private KVJSONMessage handleIncomingPrimaryUpdate(KVJSONMessage msg){
        KVJSONMessage ret = new KVJSONMessage();
        ret.setExtendStatus(eKVExtendStatusType.REPLICA_OK);
        try {
            serverinstance.putKV(msg.getKey(),msg.getValue());
        } catch (Exception e) {
            ret.setExtendStatus(eKVExtendStatusType.REPLICA_ERROR);
        }
        return ret;
    }
    private KVJSONMessage handleDepartingPrimaryUpdate(KVJSONMessage msg){
        if(serverinstance.getClusterUpdateDaemon()!=null){
            try {
                serverinstance.getClusterUpdateDaemon().queueMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private KVJSONMessage handleClusterOperation(KVJSONMessage msg){
        KVClusterOperationMessage clusterMsg = KVClusterOperationMessage.fromKVJSONMessage(msg);
        KVJSONMessage ret = new KVJSONMessage();
        ret.setExtendStatus(eKVExtendStatusType.REPLICA_ERROR);
        switch (clusterMsg.getOperationType()){
            case EXIT:{
                if(serverinstance.joinCluster(clusterMsg.getTargetCluster())){
                    ret.setExtendStatus(eKVExtendStatusType.REPLICA_OK);
                }
                break;
            }
            case JOIN:{
                if(serverinstance.exitCluster(clusterMsg.getTargetCluster())){
                    ret.setExtendStatus(eKVExtendStatusType.REPLICA_OK);
                }
                break;
            }
        }
        return ret;
    }
    private KVJSONMessage handlePrimaryDeclaration(KVJSONMessage msg){
        KVPrimaryDeclarationMessage declarationMessage = KVPrimaryDeclarationMessage.fromKVJSONMessage(msg);
        serverinstance.getMetadataController().setPrimary(declarationMessage.getClusterID(),declarationMessage.getPrimaryID());
        return null;
    }
    /**
     * Check if the key is valid
     * @param key
     * @return
     */
    private  boolean isKeyValid(String key){
        return !key.matches("") && !whitespacechecker.matcher(key).find() && key.length() <= 20;
    }
    private boolean isValidDeleteIdentifier(String value){
        return value.matches(DELETE_IDENTIFIER) || value.matches("");
    }
    private boolean isKeyResponsible(String key, boolean isWrite){
        return serverinstance.isKeyResponsible(key, isWrite);
    }
}
