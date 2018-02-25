package app_kvServer;

import common.communication.KVCommunicationModule;
import common.enums.eKVExtendStatusType;
import common.enums.eKVLogLevel;
import common.messages.KVJSONMessage;
import common.messages.KVMessage;
import logger.KVOut;

import java.io.IOException;
import java.net.SocketException;
import java.util.regex.Pattern;

import static common.messages.KVMessage.StatusType.*;

public class KVServerInstance implements Runnable {

    private KVCommunicationModule communicationModule;
    private IKVServer serverinstance;
    private KVOut kv_out = new KVOut("server");
    private boolean isRunning;
    private static final String DELETE_IDENTIFIER = "null";
    private Pattern whitespacechecker = Pattern.compile("\\s");

    public KVServerInstance(KVCommunicationModule communicationModule, IKVServer server){
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
                KVJSONMessage in_msg = communicationModule.receiveMessage();
                communicationModule.send(handleMessage(in_msg));
            }
            catch (SocketException e){
                isRunning = false;
            }
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
    public KVMessage handleMessage(KVJSONMessage in_message) {
        String out = String.format("Received inbound message, key: %s, value: %s,Operator: %d",
                in_message.getKey(),in_message.getValue(),in_message.getExtendStatusType().getValue());
        kv_out.println_debug(out);
        eKVExtendStatusType statusType = in_message.getExtendStatusType();
        KVJSONMessage retMessage = communicationModule.getEmptyMessage();
        switch (statusType){
            case GET:{
                return handleGet(in_message);
            }
            case PUT:{
                return handlePut(in_message);
            }
            case ECHO:{
                return in_message;
            }
            default:{
                retMessage.setExtendStatus(eKVExtendStatusType.UNKNOWN_ERROR);
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

    private KVMessage handleDelete(KVJSONMessage msg){
        KVJSONMessage emptyMessage = communicationModule.getEmptyMessage();
        if(isKeyValid(msg.getKey())){
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
            emptyMessage.setStatus(DELETE_ERROR);
        }
        return emptyMessage;
    }
    private KVMessage handlePut(KVJSONMessage msg){
        if(isValidDeleteIdentifier(msg.getValue())){
            return handleDelete(msg);
        }
        else{
            KVJSONMessage response = communicationModule.getEmptyMessage();
            if(isKeyValid(msg.getKey())){
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
            else {
                response.setStatus(PUT_ERROR);
            }
            return response;
        }
    }
    private KVMessage handleGet(KVJSONMessage msg){
        KVJSONMessage response = communicationModule.getEmptyMessage();
        if(isKeyValid(msg.getKey())){
            try {
                String ret = serverinstance.getKV(msg.getKey());
                response.setKey(msg.getKey());
                response.setValue(ret);
                response.setStatus(GET_SUCCESS);
            } catch (Exception e) {
                response.setStatus(GET_ERROR);
            }
        }
        else{
            response.setStatus(GET_ERROR);
        }
        return response;
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
}
