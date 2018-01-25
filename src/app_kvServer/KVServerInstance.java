package app_kvServer;

import common.communication.KVCommunicationModule;
import common.messages.KVMessage;
import logger.KVOut;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static common.messages.KVMessage.StatusType.*;

public class KVServerInstance implements Runnable {

    private KVCommunicationModule communicationModule;
    private IKVServer serverinstance;
    private KVOut kv_out = new KVOut();
    private boolean isRunning;
    private static final String DELETE_IDENTIFIER = "null";

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
                KVMessage in_msg = communicationModule.receiveMessage();
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
    public KVMessage handleMessage(KVMessage in_message){
        KVMessage.StatusType statusType = in_message.getStatus();
        KVMessage retMessage = communicationModule.getEmptyMessage();
        switch (statusType){
            case GET:{
                try{
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
                    String value = serverinstance.getKV(in_message.getKey());
                    if(!value.matches(update_value)) {
                        if(update_value.matches(DELETE_IDENTIFIER)){
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
                    if(update_value.matches(DELETE_IDENTIFIER)) {
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
                    if(in_message.getValue().matches(DELETE_IDENTIFIER)){
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
                retMessage.setStatus(KVMessage.StatusType.UNKNOWN_ERROR);
            }
        }
        return retMessage;
    }
}
