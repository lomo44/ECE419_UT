package app_kvECS.Commands;

import app_kvECS.ECSClient;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import common.messages.KVJSONMessage;

public class KVCommandSetServerJarPath extends KVCommand<ECSClient>{
    String executablePath;

    public KVCommandSetServerJarPath(String path) {
        super(KVCommandPattern.KVCommandType.SET_SERVER_JAR_PATH);
        this.executablePath = path;
    }

    @Override
    public KVJSONMessage execute(ECSClient Instance) {
        Instance.setDeployedServerJarPath(executablePath);
        KVJSONMessage msg = new KVJSONMessage();
        msg.setValue(executablePath);
        return msg;
    }

    @Override
    public void handleResponse(KVJSONMessage response) {
        System.out.printf("Remote Server Executable Path set to %s\n",executablePath);
    }
}
