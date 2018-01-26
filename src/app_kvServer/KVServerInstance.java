package app_kvServer;

import common.communication.KVCommunicationModule;
import common.enums.eKVExtendStatusType;
import common.enums.eKVLogLevel;
import common.messages.KVJSONMessage;
import common.messages.KVMessage;
import logger.KVOut;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

import static common.messages.KVMessage.StatusType.*;

public class KVServerInstance implements Runnable {

    private KVCommunicationModule communicationModule;
    private IKVServer serverinstance;
    private KVOut kv_out = new KVOut();
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
        isRunning = true;
        while(communicationModule.isConnected() && isRunning){
            try {
                KVJSONMessage in_msg = communicationModule.receiveMessage();
                communicationModule.send(handleMessage(in_msg));
            }
            catch (SocketTimeoutException e){
                //System.out.println("Received timeout");
            }
            catch (SocketException e){
                isRunning = false;
            }
            catch(Exception e){

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
    public KVMessage handleMessage(KVJSONMessage in_message){
        String out = String.format("Received inbound message, key: %s, value: %s,Operator: %d",
                in_message.getKey(),in_message.getValue(),in_message.getExtendStatusType().getValue());
        kv_out.println_debug(out);
        eKVExtendStatusType statusType = in_message.getExtendStatusType();
        KVJSONMessage retMessage = communicationModule.getEmptyMessage();
        switch (statusType){
            case GET:{
                try{
                    if(!isKeyValid(in_message.getKey()))
                        throw new Exception();
                    kv_out.println_info("Received GET request from client.");
                    String value = serverinstance.getKV(in_message.getKey());
                    retMessage.setStatus(KVMessage.StatusType.GET_SUCCESS);
                    retMessage.setKey(in_message.getKey());
                    retMessage.setValue(value);
                    kv_out.println_debug("Found corresponding key/value store for GET request.");
                }
                catch (Exception e){
                    // Exception happened when query for value
                    retMessage.setStatus(KVMessage.StatusType.GET_ERROR);
                    kv_out.println_debug("Could not find corresponding key/value store for GET request.");
                }
                break;
            }
            case PUT:{
                String update_value = in_message.getValue();
                try {
                    if(!isKeyValid(in_message.getKey()))
                        throw new Exception();
                    String value = serverinstance.getKV(in_message.getKey());
                    if(!value.matches(update_value)) {
                        if(update_value.matches(DELETE_IDENTIFIER) || update_value.matches("")){
                            kv_out.println_info("Received DELETE request from client.");
                            retMessage.setStatus(DELETE_SUCCESS);
                        }
                        else{
                            kv_out.println_info("Received PUT_UPDATE request from client.");
                            retMessage.setStatus(PUT_UPDATE);
                            retMessage.setKey(in_message.getKey());
                            retMessage.setValue(in_message.getValue());
                        }
                    }
                    else {
                        kv_out.println_info("Received PUT request from client.");
                        retMessage.setStatus(PUT_SUCCESS);
                        break;
                    }
                } catch (Exception e) {
                    if(!isKeyValid(in_message.getKey())){
                        retMessage.setStatus(PUT_ERROR);
                        return retMessage;
                    }
                    if(update_value.matches(DELETE_IDENTIFIER) || update_value.matches("")) {
                        retMessage.setStatus(DELETE_ERROR);
                        break;
                    }
                    else {
                        retMessage.setStatus(PUT_SUCCESS);
                    }
                }
                try {
                    serverinstance.putKV(in_message.getKey(),in_message.getValue());
                    kv_out.println_debug("Inserted/updated/deleted corresponding key/value store for PUT request.");
                } catch (Exception e) {
                    if(in_message.getValue().matches(DELETE_IDENTIFIER) || update_value.matches("")){
                        retMessage.setStatus(DELETE_ERROR);
                        kv_out.println_debug("Could not delete corresponding key/value store for DELETE request.");
                    }else {
                        retMessage.setStatus(PUT_ERROR);
                        kv_out.println_debug("Could not insert/update corresponding key/value store for PUT/PUT_UPDATE requst.");
                    }
                }
                break;
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

    public boolean isKeyValid(String key){
        return !key.matches("") && !whitespacechecker.matcher(key).find() && key.length() <= 20;
    }
}
