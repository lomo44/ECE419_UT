package common.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import common.enums.eKVLogLevel;
import common.messages.KVJSONMessage;
import common.messages.KVMessage;
import logger.KVOut;

public class KVCommunicationModule {
    // Communication module for both server and client
    private Socket privateSocket;
    private KVOut kv_out = null;
    private boolean isInitialized = false;
    public KVCommunicationModule(Socket in_Socket,String hint) {
        privateSocket = in_Socket;
        this.kv_out = new KVOut(hint);
	}

    /**
     * Create a empty KVMessage
     * @return
     */
	public static KVJSONMessage getEmptyMessage(){
		return new KVJSONMessage();
	}

    /**
     * Send a KVMessage through the module
     * @param in_Message outbound message
     * @throws SocketException will be thrown if socket is closed
     */
    public void send(KVMessage in_Message) throws SocketException{
        if(!privateSocket.isClosed()){
            OutputStream outputStream;
            try {
                outputStream = privateSocket.getOutputStream();
                DataOutputStream data_out = new DataOutputStream(outputStream);
                ((KVJSONMessage)in_Message).setSendTime();
                byte[] out = ((KVJSONMessage)in_Message).toBytes();
                data_out.write(out.length);
                data_out.write(out);
                data_out.flush();
                kv_out.println_info("Sent message to "+getSocket().getInetAddress().getHostName()+" at port "+getSocket().getPort());
            }
            catch (IOException e) {
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
    public KVJSONMessage receiveMessage() throws SocketException, InterruptedException {
        if(!privateSocket.isClosed()){
            try {
                InputStream in_Message = privateSocket.getInputStream();
                DataInputStream dInputStream = new DataInputStream(in_Message);
                KVJSONMessage ret = getEmptyMessage();
                int bytelength = dInputStream.read();
                if(bytelength >= 0){
                    byte[] array = new byte[bytelength];
                    dInputStream.read(array);
                    ret.fromBytes(array);
                    kv_out.println_info("Received message from "+getSocket().getInetAddress().getHostName()+" at port "+getSocket().getPort());
                }
                else{
                    throw new InterruptedException();
                }
                return ret;

            }
            catch (IOException e) {
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
		return !privateSocket.isClosed();
	}

    /**
     * Retrieve the socket of the communication module
     * @return
     */
	private Socket getSocket() {
	    return privateSocket;
    }

    /**
     * Close the communication module
     * @throws IOException
     */
	public  void close() throws IOException {
		this.privateSocket.close();
	}

    /**
     * Set the log level of the communication module
     * @param outputlevel
     * @param logLevel
     */
	public void setLogLevel(eKVLogLevel outputlevel, eKVLogLevel logLevel){
	    kv_out.changeLogLevel(logLevel);
	    kv_out.changeOutputLevel(outputlevel);
    }
}
