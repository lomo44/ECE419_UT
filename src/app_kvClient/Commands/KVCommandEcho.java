package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

public class KVCommandEcho extends KVCommand {
    public KVCommandEcho() {
        super(KVCommandPattern.KVCommandType.ECHO);
    }

    @Override
    public KVJSONMessage execute(KVClient clientInstance) {
        KVJSONMessage newmsg = clientInstance.getStore().createEmptyMessage();
        newmsg.setExtendStatus(eKVExtendStatusType.ECHO);
        try {
            newmsg = (KVJSONMessage)clientInstance.getStore().send(newmsg);
        } catch (Exception e) {
        }
        finally{
            return newmsg;
        }
    }
    @Override
    public void handleResponse(KVJSONMessage response){
        if(response!=null){
            if(response.getExtendStatusType() == eKVExtendStatusType.ECHO){
                System.out.println(response.getStatus().toString());
            }
        }
        else{
            kv_out.println_error("Failed to retrieve echo message.");
        }
    }
}
