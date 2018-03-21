package common.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import common.KVMessage;
import common.enums.eKVLogLevel;
import common.messages.KVJSONMessage;
import common.networknode.KVNetworkNode;
import logger.KVOut;

import static java.lang.Math.min;

public class KVCommunicationModule extends IKVCommunicationModule {
    // Communication module for both server and client
    private Socket privateSocket;
    private KVOut kv_out = null;
    private byte[] internalBuffer = new byte[2000000];

    public KVCommunicationModule(KVNetworkNode node) throws IOException {
        this(node.createSocket(),node.getUID());
    }

    public KVCommunicationModule(Socket in_Socket, String hint) {
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
    @Override
    public void send(KVJSONMessage in_Message) throws SocketException{
        if(!privateSocket.isClosed()){
            try {
                in_Message.setSendTime();
                byte[] out = in_Message.toBytes();
                bufferedWrite(out);
                //kv_out.println_info("Sent message to "+getSocket().getInetAddress().getHostName()+" at port "+getSocket().getPort());
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
    @Override
    public KVJSONMessage receive() throws SocketException{
        if(!privateSocket.isClosed()){
            try {
                KVJSONMessage ret = new KVJSONMessage();
                ret.fromBytes(internalBuffer,0,bufferedRead());
                //kv_out.println_info("Received message from "+getSocket().getInetAddress().getHostName()+" at port "+getSocket().getPort());
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
    @Override
	public boolean isConnected(){
		return !privateSocket.isClosed();
	}

    /**
     * Retrieve the socket of the communication module
     * @return
     */
	public Socket getSocket() {
	    return privateSocket;
    }

    /**
     * Close the communication module
     * @throws IOException
     */
    @Override
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

    /**
     * Helping function to buffered read from the socket
     * @return output byte array
     * @throws IOException thrown when there is problem getting the InputStream
     */
    private int bufferedRead() throws IOException {
        InputStream in_Message = privateSocket.getInputStream();
        DataInputStream dInputStream = new DataInputStream(in_Message);
        int length = dInputStream.readInt();
        return bufferedRead(internalBuffer,length,dInputStream);
    }

    /**
     * Heplong function for buffered write to the socket
     * @param output output buffer
     * @throws IOException thrown when the there is a problme getting the output stream
     */
    private void bufferedWrite(byte[] output) throws IOException {
        OutputStream outputStream = privateSocket.getOutputStream();
        DataOutputStream data_out = new DataOutputStream(outputStream);
        bufferedWrite(output,data_out);
    }
}
