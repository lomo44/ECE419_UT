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
        KVMessage ret = new KVJSONMessage();
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
            kv_out.println_info("Successfully connected to server.");
        }
        else{
            kv_out.println_error("Failed to connect to server.");
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
