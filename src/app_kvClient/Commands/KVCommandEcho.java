package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.communication.KVCommunicationModule;
import common.messages.KVMessage;

public class KVCommandEcho extends KVCommand {
    public KVCommandEcho() {
        super(KVCommandPattern.KVCommandType.ECHO);
    }

    @Override
    public KVMessage execute(KVClient clientInstance) {
        KVMessage newmsg = clientInstance.getStore().createEmptyMessage();
        newmsg.setStatus(KVMessage.StatusType.ECHO);
        KVMessage inbound = null;
        try {
            inbound = clientInstance.getStore().send(newmsg);
        } catch (Exception e) {
        }
        finally{
            return inbound;
        }
    }
    @Override
    public void handleResponse(KVMessage response){
        if(response!=null){
            if(response.getStatus() == KVMessage.StatusType.ECHO){
                System.out.println(response.getStatus().toString());
            }
        }
        else{
            System.out.println("Failed to retrieve echo");
        }
    }
}
