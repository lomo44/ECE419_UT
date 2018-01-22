package common.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import common.messages.KVJSONMessage;
import common.messages.KVMessage;

public class KVCommunicationModule {
	// Communication module for both server and clietn
	private Socket privateSocket;
	private int timeout;
	private boolean isInitialized = false;
	public KVCommunicationModule(Socket in_Socket, int timeout) {
		privateSocket = in_Socket;
		this.timeout = timeout;

	}

    /**
     * Create a empty KVMessage
     * @return
     */
	public static KVMessage getEmptyMessage(){
		return new KVJSONMessage();
	}

    /**
     * Send a KVMessage through the module
     * @param in_Message outbound message
     * @throws SocketException will be thrown if socket is closed
     */
	public void send(KVMessage in_Message) throws SocketException {
		if(!isInitialized){
			initialize();
		}
		if(!privateSocket.isClosed()){
			OutputStream outputStream;
			try {
				outputStream = privateSocket.getOutputStream();
				DataOutputStream data_out = new DataOutputStream(outputStream);
				byte[] out = in_Message.toBytes();
				data_out.write(out.length);
				data_out.write(out);
			} catch (IOException e) {
				e.printStackTrace();
				throw new SocketException();
			}
		}
		else {
			throw new SocketException();
		}
	}

    /**
     * receive message
     * @return KVMessage
     * @throws SocketException thrown if socket is closed
     */
	public KVMessage receiveMessage() throws SocketException {
		if(!isInitialized){
			initialize();
		}
		if(!privateSocket.isClosed()){
			try {
				InputStream in_Message = privateSocket.getInputStream();
				DataInputStream dInputStream = new DataInputStream(in_Message);
				KVMessage ret = KVCommunicationModule.getEmptyMessage();
				int bytelength = dInputStream.read();
				byte[] array = new byte[bytelength];
				dInputStream.read(array);
				ret.fromBytes(array);
				return ret;
			} catch (IOException e) {
				//e.printStackTrace();
				throw new SocketException();
			}
		}
		else {
			throw new SocketException();
		}
	}

    /**
     * Check if the communication module is connected
     * @return
     */
	public boolean isConnected(){
		return privateSocket.isConnected();
	}

	public void initialize() throws SocketException {
		if(timeout > 0){
			this.privateSocket.setSoTimeout(this.timeout);
		}
	}
	public  void close() throws IOException {
		this.privateSocket.close();
	}
}
