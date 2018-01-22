package app_kvClient.Commands;

import app_kvClient.CommandPatterns.KVCommandPattern;
import app_kvClient.KVClient;

public class KVCommandConnect extends KVCommand{
    public KVCommandConnect() {
        super(KVCommandPattern.KVCommandType.CONNECT);
    }

    @Override
    public void execute(KVClient clientInstance) {
        try {
            clientInstance.newConnection(getHostName(),Integer.parseInt(getPort()));
        } catch (Exception e) {
            e.printStackTrace();
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
