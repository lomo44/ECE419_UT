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

import static java.lang.Math.min;

public class KVCommunicationModule {
    // Communication module for both server and client
    private Socket privateSocket;
    private KVOut kv_out = null;
    private int bufferedSize = 512;
    private byte[] internalBuffer = new byte[2000000];

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
    public void send(KVMessage in_Message) throws SocketException{
        if(!privateSocket.isClosed()){
            OutputStream outputStream;
            try {
                ((KVJSONMessage)in_Message).setSendTime();
                byte[] out = ((KVJSONMessage)in_Message).toBytes();
                bufferedWrite(out);
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
    public KVJSONMessage receiveMessage() throws SocketException{
        if(!privateSocket.isClosed()){
            try {
                KVJSONMessage ret = getEmptyMessage();
                ret.fromBytes(internalBuffer,0,bufferedRead());
                kv_out.println_info("Received message from "+getSocket().getInetAddress().getHostName()+" at port "+getSocket().getPort());
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

    /**
     * Overwrite the current buffer size for reading and writing the data out
     * @param bufferedSize new buffer size
     */
    public void setBufferedSize(int bufferedSize) {
        this.bufferedSize = bufferedSize;
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
	    int bytesCount = length;
	    int outputPosition = 0;
	    int chunksize;
	    int byteRead;
	    while (bytesCount > 0){
            chunksize = min(bytesCount, bufferedSize);
            byteRead = dInputStream.read(internalBuffer,outputPosition,chunksize);
            if(byteRead < 0){
                throw new IOException();
            }
            outputPosition+=byteRead;
            bytesCount-=byteRead;
        }
        return length;
    }

    /**
     * Heplong function for buffered write to the socket
     * @param output output buffer
     * @throws IOException thrown when the there is a problme getting the output stream
     */
    private void bufferedWrite(byte[] output) throws IOException {
        OutputStream outputStream = privateSocket.getOutputStream();
        DataOutputStream data_out = new DataOutputStream(outputStream);
        int bytesToBeWritten = output.length;
        int writePosition = 0;
        int chunksize;
        // First write out the length of the buffer
        data_out.writeInt(output.length);
        while(bytesToBeWritten > 0){
            chunksize = min(bytesToBeWritten, bufferedSize);
            data_out.write(output,writePosition,chunksize);
            writePosition+=chunksize;
            bytesToBeWritten-=chunksize;
        }
        data_out.flush();
    }
}
