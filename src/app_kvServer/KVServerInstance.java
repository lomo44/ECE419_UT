package app_kvServer;

import common.communication.KVCommunicationModule;
import common.messages.KVMessage;

public class KVServerInstance implements Runnable {

    private KVCommunicationModule communicationModule;
    private KVServer serverinstance;

    public KVServerInstance(KVCommunicationModule communicationModule, KVServer server){
        this.communicationModule = communicationModule;
        serverinstance = server;
    }

    /**
     * Common implementation for thread
     */
    @Override
    public void run() {
        while(communicationModule.isConnected()){
            try {
                KVMessage in_msg = communicationModule.receiveMessage();
                communicationModule.send(handleMessage(in_msg));
            }
            catch(Exception e){

            }
        }
    }

    /**
     * Handle a input message and generate a output message
     * @param in_message inbound message
     * @return KVMessage outbound message
     */
    public KVMessage handleMessage(KVMessage in_message){
        KVMessage.StatusType statusType = in_message.getStatus();
        KVMessage retMessage = KVCommunicationModule.getEmptyMessage();
        switch (statusType){
            case GET:{
                if(serverinstance.inCache(in_message.getKey())){
                    try{
                        String value = serverinstance.getKV(in_message.getKey());
                        retMessage.setStatus(KVMessage.StatusType.GET_ERROR);
                        retMessage.setKey(in_message.getKey());
                        retMessage.setValue(value);
                    }
                    catch (Exception e){
                        // Exception happened when quarry for value
                        retMessage.setStatus(KVMessage.StatusType.GET_SUCCESS);
                    }
                }
                else{
                    // Key is not in storage, return error message
                    retMessage.setStatus(KVMessage.StatusType.GET_ERROR);
                }
            }
            case PUT:{
                try{
                    serverinstance.putKV(in_message.getKey(),in_message.getValue());
                    retMessage.setStatus(KVMessage.StatusType.PUT_SUCCESS);
                }
                catch (Exception e){
                    retMessage.setStatus(KVMessage.StatusType.PUT_ERROR);
                }
            }
            default:{
                retMessage.setStatus(KVMessage.StatusType.UNKNOWN_ERROR);
            }
        }
        return retMessage;
    }
}
