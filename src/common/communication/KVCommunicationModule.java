package common.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.xml.crypto.Data;

import common.messages.KVJSONMessage;
import common.messages.KVMessage;

public class KVCommunicationModule {
	// Communication module for both server and clietn
	private Socket privateSocket;
	public KVCommunicationModule(Socket in_Socket) {
		privateSocket = in_Socket;
	}
	public void send(KVMessage in_Message) throws SocketException {
		if(!privateSocket.isClosed()){
			OutputStream outputStream;
			try {
				outputStream = privateSocket.getOutputStream();
				DataOutputStream data_out = new DataOutputStream(outputStream);
				byte[] out = in_Message.toBytes();
				data_out.write(out.length);
				data_out.write(in_Message.toBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new SocketException();
			}
		}
		else {
			throw new SocketException();
		}
	}
	public KVJSONMessage receiveKVJSONMessage() throws SocketException {
		if(!privateSocket.isClosed()){
			try {
				InputStream in_Message = privateSocket.getInputStream();
				DataInputStream dInputStream = new DataInputStream(in_Message);
				KVJSONMessage ret = new KVJSONMessage();
				int bytelength = dInputStream.read();
				byte[] array = new byte[bytelength];
				dInputStream.read(array);
				ret.fromBytes(array);
				return ret;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new SocketException();
			}
		}
		else {
			throw new SocketException();
		}
	}
}
