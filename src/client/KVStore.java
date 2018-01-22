package client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.net.SocketException;

import org.apache.log4j.Logger;

import common.messages.KVMessage;
import common.communication.KVCommunicationModule;

public class KVStore implements KVCommInterface {
	
	private Logger logger = Logger.getRootLogger();
	private Set<app_kvClient.IKVClient> listeners;
	private boolean running;

	private String serverAddress;
	private int serverPort;
	private Socket clientSocket;
	private KVCommunicationModule communicationModule;
	
	/**
	 * Initialize KVStore with address and port of KVServer
	 * @param address the address of the KVServer
	 * @param port the port of the KVServer
	 */
	public KVStore(String address, int port) {
		serverAddress = address;
		serverPort = port;
		listeners = new HashSet<app_kvClient.IKVClient>();
	}

	//TODO: Handle connection exception
	@Override
	public void connect() throws Exception {
		clientSocket = new Socket(serverAddress, serverPort);
		communicationModule = new KVCommunicationModule(clientSocket,500);
		setRunning(true);
		logger.info("Connection established");

	}

	public String getServerAddress() {
		return serverAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void disconnect() throws IOException {
		setRunning(false);
		if (clientSocket != null) {
			clientSocket.close();
			communicationModule = null;
			clientSocket = null;
		}
	}

	@Override
	public KVMessage put(String key, String value) throws SocketException {
		KVMessage newmessage = createEmptyMessage();
		newmessage.setValue(value);
		newmessage.setKey(key);
		newmessage.setStatus(KVMessage.StatusType.PUT);
		communicationModule.send(newmessage);
		return communicationModule.receiveMessage();
	}

	@Override
	public KVMessage get(String key) throws Exception {
		KVMessage newmessage = createEmptyMessage();
		newmessage.setKey(key);
		newmessage.setValue("");
		newmessage.setStatus(KVMessage.StatusType.GET);
		communicationModule.send(newmessage);
		return communicationModule.receiveMessage();
	}

	@Override
	public KVMessage send(KVMessage outboundmsg) throws SocketException {
		communicationModule.send(outboundmsg);
		return communicationModule.receiveMessage();
	}

	@Override
	public KVMessage createEmptyMessage() {
		return communicationModule.getEmptyMessage();
	}
}
