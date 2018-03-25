package app_kvECS.Commands;

import common.command.KVCommand;
import common.command.KVCommandPattern;
import app_kvECS.ECSClient;
import common.messages.KVJSONMessage;


public class KVCommandSetKeyPath extends KVCommand<ECSClient> {
	String keypath;
	
	public KVCommandSetKeyPath(String path) {
		super(KVCommandPattern.KVCommandType.SET_KEYPATH);
		this.keypath = path;
	}

	@Override
	public KVJSONMessage execute(ECSClient clientInstance) {
		clientInstance.setKeyPath(keypath);
		KVJSONMessage msg = new KVJSONMessage();
        msg.setValue(keypath);
		return msg;
	}

	@Override
	public void handleResponse(KVJSONMessage response) {
		System.out.printf("SSH Key Path set to %s\n",keypath);
	}
}