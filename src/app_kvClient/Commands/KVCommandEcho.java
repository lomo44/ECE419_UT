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
    public void execute(KVClient clientInstance) {
        KVMessage newmsg = KVCommunicationModule.getEmptyMessage();
        newmsg.setStatus(KVMessage.StatusType.ECHO);
        try {
            KVMessage inbound = clientInstance.getStore().send(newmsg);
            if(inbound.getStatus() == KVMessage.StatusType.ECHO){
                System.out.println(inbound.getStatus().toString());
            }
        } catch (Exception e) {
        }
    }
}
