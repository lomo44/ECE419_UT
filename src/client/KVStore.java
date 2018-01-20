package client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.net.SocketException;

import org.apache.log4j.Logger;

import common.messages.KVMessage;
import common.communication.KVCommunicationModule;
import app_kvClient.IKVClient;

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

	/**
	 * Initializes and starts the client connection. 
	 * Loops until the connection is closed or aborted by the client.
	 */
	// public void run() {
	// 	try {
	// 		while (isRunning()) {
	// 			try {
	// 				KVMessage incoming_msg = communicationModule.receiveMessage();
	// 				String incoming_str = handleMessage(incoming_msg);
	// 				for (IKVClient listener : listeners) {
	// 					listener.printMessage(incoming_str);
	// 				}
	// 			} catch (IOException ioe) {
	// 				if (isRunning()) {
	// 					logger.error("Connection lost!");
	// 					try {
	// 						disconnect();
	// 					} catch (IOException e) {
	// 						logger.error("Unable to close connection!");
	// 					}
	// 				}
	// 			}
	// 		}
	// 	} catch (IOException ioe) {
	// 		logger.error("Connection could not be established!");
	// 	} finally {
	// 		if (isRunning()) {
	// 			disconnect();
	// 		}
	// 	}
	// }

	public String handleMessage(KVMessage incoming_msg) {
		KVMessage.StatusType statusType = incoming_msg.getStatus();
		StringBuilder incoming_str = new StringBuilder();
		switch (statusType) {
			case PUT_SUCCESS:{
				// TODO
			}
			case PUT_UPDATE:{
				// TODO
			}
			case PUT_ERROR:{
				// TODO
			}
			case GET_SUCCESS:{
				// TODO
			}
			case GET_ERROR:{
				// TODO
			}
			case DELETE_SUCCESS:{
				// TODO
			}
			case DELETE_ERROR:{
				// TODO
			}
			case UNKNOWN_ERROR:{
				// TODO
			}
			default:{
				// TODO (same as UNKNOWN_ERROR)
			}
		}
		return incoming_str.toString();
	}

	@Override
	public void connect() throws Exception {
		clientSocket = new Socket(serverAddress, serverPort);
		communicationModule = new KVCommunicationModule(clientSocket);
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

	public void addListener(IKVClient listener) {
		listeners.add(listener);
	}

	@Override
	public void disconnect() {
		setRunning(false);
		logger.info("Try to close connection ...");
		if (clientSocket != null) {
				try {
					clientSocket.close();
					communicationModule = null;
					clientSocket = null;
					logger.info("Connection closed!");
				} catch (IOException e) {
					logger.error("Unable to close connection!");
				}
		}
	}

	private synchronized void failGracefully() {
		if (isRunning()) {
			logger.error("Connection lost!");
			disconnect();
		}
	}

	@Override
	public KVMessage put(String key, String value) throws Exception {
		KVMessage outgoing_msg = communicationModule.getEmptyMessage();
		KVMessage ret_msg = communicationModule.getEmptyMessage();
		outgoing_msg.setKey(key);
		outgoing_msg.setValue(value);
		outgoing_msg.setStatus(KVMessage.StatusType.PUT);
		while (isRunning()) {
			try {
				communicationModule.send(outgoing_msg);
			} catch (SocketException e) {
				failGracefully();
			}
		}

		while (isRunning()) {
			try {
				KVMessage incoming_msg = communicationModule.receiveMessage();
				ret_msg.setKey(incoming_msg.getKey());
				ret_msg.setValue(incoming_msg.getValue());
				ret_msg.setStatus(incoming_msg.getStatus());
			} catch (SocketException e) {
				failGracefully();
			}
		}
		
		return ret_msg;
	}

	@Override
	public KVMessage get(String key) throws Exception {
		KVMessage outgoing_msg = communicationModule.getEmptyMessage();
		KVMessage ret_msg = communicationModule.getEmptyMessage();
		outgoing_msg.setKey(key);
		outgoing_msg.setStatus(KVMessage.StatusType.GET);

		while (isRunning()) {
			try {
				communicationModule.send(outgoing_msg);
			} catch (IOException e) {
				failGracefully();
			}
		}

		while (isRunning()) {
			try {
				KVMessage incoming_msg = communicationModule.receiveMessage();
				ret_msg.setKey(incoming_msg.getKey());
				ret_msg.setValue(incoming_msg.getValue());
				ret_msg.setStatus(incoming_msg.getStatus());
			} catch (IOException e) {
				failGracefully();
			}
		}
		
		return ret_msg;
	}
}
