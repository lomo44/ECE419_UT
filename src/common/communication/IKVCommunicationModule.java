package common.communication;

import common.KVMessage;
import common.messages.KVJSONMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import static java.lang.Math.min;

public abstract class IKVCommunicationModule {
    boolean isConnected = false;
    private int bufferedSize = 512;

    public abstract void send(KVJSONMessage msg) throws Exception;
    public abstract KVJSONMessage receive() throws Exception;
    public abstract void close() throws Exception;
    public boolean isConnected(){return isConnected;}
    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void bufferedWrite(byte[] data, DataOutputStream output) throws IOException {
        int bytesToBeWritten = data.length;
        int writePosition = 0;
        int chunksize;
        // First write out the length of the buffer
        output.writeInt(data.length);
        while(bytesToBeWritten > 0){
            chunksize = min(bytesToBeWritten, bufferedSize);
            output.write(data,writePosition,chunksize);
            writePosition+=chunksize;
            bytesToBeWritten-=chunksize;
        }
        output.flush();
    }
    public int bufferedRead(byte[] buffer, int length, DataInputStream input) throws IOException {
        int bytesCount = length;
        int outputPosition = 0;
        int chunksize;
        int byteRead;
        while (bytesCount > 0){
            chunksize = min(bytesCount, bufferedSize);
            byteRead = input.read(buffer,outputPosition,chunksize);
            if(byteRead < 0){
                throw new IOException();
            }
            outputPosition+=byteRead;
            bytesCount-=byteRead;
        }
        return length;
    }
    /**
     * Overwrite the current buffer size for reading and writing the data out
     * @param bufferedSize new buffer size
     */
    public void setBufferedSize(int bufferedSize) {
        this.bufferedSize = bufferedSize;
    }
}
