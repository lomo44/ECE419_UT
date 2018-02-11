package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;
import common.enums.eKVExtendStatusType;
import common.messages.KVJSONMessage;

public class KVCommandConnect extends KVCommand{
    public KVCommandConnect() {
        super(KVCommandPattern.KVCommandType.CONNECT);
    }

    @Override
    public KVJSONMessage execute(KVClient clientInstance) {
        KVJSONMessage ret = new KVJSONMessage();
        try {
            clientInstance.newConnection(getHostName(),Integer.parseInt(getPort()));
            ret.setExtendStatus(eKVExtendStatusType.CONNECT_SUCCESS);
        } catch (Exception e) {
            ret.setExtendStatus(eKVExtendStatusType.CONNECT_FAIL);
        }
        return ret;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        if(response.getExtendStatusType() == eKVExtendStatusType.CONNECT_SUCCESS){
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
        return getValue("HostName");
    }
    public void setPort(String port){
        set("PortNumber", port);
    }
    public String getPort(){
        return getValue("PortNumber");
    }
}
