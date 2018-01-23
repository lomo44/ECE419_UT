package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.messages.KVJSONMessage;
import common.messages.KVMessage;

public class KVCommandConnect extends KVCommand{
    public KVCommandConnect() {
        super(KVCommandPattern.KVCommandType.CONNECT);
    }

    @Override
    public KVMessage execute(KVClient clientInstance) {
        KVMessage ret = clientInstance.getStore().createEmptyMessage();
        try {
            clientInstance.newConnection(getHostName(),Integer.parseInt(getPort()));
            ret.setStatus(KVMessage.StatusType.CONNECT_SUCCESS);
        } catch (Exception e) {
            ret.setStatus(KVMessage.StatusType.CONNECT_FAIL);
        }
        return ret;
    }

    @Override
    public void handleResponse(KVMessage response) {
        if(response.getStatus() == KVMessage.StatusType.CONNECT_SUCCESS){
            System.out.println("Connection Success.");
        }
        else{
            System.out.println("Connection Failed.");
        }
    }

    public void setHostName(String hostname){
        set("HostName",hostname);
    }
    public String getHostName(){
        return get("HostName");
    }
    public void setPort(String port){
        set("PortNumber", port);
    }
    public String getPort(){
        return get("PortNumber");
    }
}
