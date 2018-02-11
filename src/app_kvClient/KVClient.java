package app_kvClient;

import java.io.IOException;

import java.io.InputStream;
import java.util.Scanner;

import app_kvClient.Commands.KVCommand;
import common.enums.eKVLogLevel;
import common.messages.KVJSONMessage;
import logger.KVOut;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import logger.LogSetup;

import client.KVStore;


public class KVClient implements IKVClient,Runnable {

    private static Logger logger = Logger.getLogger("client");
    private static final String PROMPT = "Client> ";
    private KVStore client = null;
    private boolean stop = false;
    private KVCommandParser cmdParser = new KVCommandParser();
    private Scanner keyboard;
    private KVClientAttribute attribute = new KVClientAttribute();
    private static KVOut kv_out = new KVOut("client");
    private eKVLogLevel outputlevel;
    private eKVLogLevel logLevel;

    /**
     * Construct a new KVClient based on the given input stream
     * @param inputStream Input stream for getting input
     */
    public KVClient(InputStream inputStream){
        keyboard = new Scanner(inputStream);
        setLogLevel(eKVLogLevel.DEBUG,eKVLogLevel.DEBUG);
    }

    /**
     * Default constructor for KVClient, will set the default input stream to System.in
     */
    public KVClient(){
        keyboard = new Scanner(System.in);
        setLogLevel(eKVLogLevel.DEBUG,eKVLogLevel.DEBUG);
    }
    /**
     * Stop the execution of current KVClient instance
     * @throws IOException thrown when cannot disconnect from socket
     */
    public void stop() throws IOException {
        disconnect();
        stop = true;
        kv_out.println_debug("Try to stop client.");
    }

    /**
     * Try to disconnect the server
     * @throws IOException thrown when cannot disconnect from socket
     */
    public void disconnect(){
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    /**
     * Check the connection status of the client
     * @return true if client is connected, false if not,
     */
    public boolean isConnected() {
        if (client != null) {
            return client.isRunning();
        }
        return false;
    }
    public String setLevel(String levelString) {
		
		if(levelString.equals(Level.ALL.toString())) {
			logger.setLevel(Level.ALL);
			return Level.ALL.toString();
		} else if(levelString.equals(Level.DEBUG.toString())) {
			logger.setLevel(Level.DEBUG);
			return Level.DEBUG.toString();
		} else if(levelString.equals(Level.INFO.toString())) {
			logger.setLevel(Level.INFO);
			return Level.INFO.toString();
		} else if(levelString.equals(Level.WARN.toString())) {
			logger.setLevel(Level.WARN);
			return Level.WARN.toString();
		} else if(levelString.equals(Level.ERROR.toString())) {
			logger.setLevel(Level.ERROR);
			return Level.ERROR.toString();
		} else if(levelString.equals(Level.FATAL.toString())) {
			logger.setLevel(Level.FATAL);
			return Level.FATAL.toString();
		} else if(levelString.equals(Level.OFF.toString())) {
			logger.setLevel(Level.OFF);
			return Level.OFF.toString();
		} else {
			return LogSetup.UNKNOWN_LEVEL;
		}
	}

    /**
     * Print help message for available commands
     */
	public void printHelp() {
        cmdParser.printHelpMessages();
	}

    /**
     * Get the client attribute of client
     * @return KVClientAttribute instance
     */
    public KVClientAttribute getAttribute() {
        return attribute;
    }

    /**
     * Get the underline KVStore instance
     * @return KVStore instance
     */
    public KVStore getStore(){
        return client;
    }

    /**
     * Execute a received command
     * @param cmdInstance command instance
     * @return message return from command
     */
    public KVJSONMessage executeCommand(KVCommand cmdInstance){
        return cmdInstance.execute(this);
    }

    /**
     * Set the log level
     * @param outputlevel System out level
     * @param logLevel logging level
     */
    public void setLogLevel(eKVLogLevel outputlevel, eKVLogLevel logLevel){
        kv_out.changeLogLevel(logLevel);
        kv_out.changeOutputLevel(outputlevel);
        if(client!=null)
            client.setLogLevel(outputlevel,logLevel);
        else {
            this.outputlevel = outputlevel;
            this.logLevel = logLevel;
        }
    }

    /**
     * Run the client
     */
    @Override
    public void run() {
        kv_out.println_debug("Client started.");
        while (!stop) {
            System.out.print(PROMPT);
            KVCommand cmdInstance = cmdParser.getParsedCommand(keyboard.nextLine());
            if (cmdInstance != null) {
                // Command line correctly parsed
                cmdInstance.handleResponse(executeCommand(cmdInstance));
            } else {
                printHelp();
            }
        }
        kv_out.println_debug("Client stopped.");
    }

    /**
     * Initiate a new connection to server
     * @param hostname hostname of the server
     * @param port port number of the server
     * @throws Exception
     */
    @Override
    public void newConnection(String hostname, int port) throws Exception {
        client = new KVStore(hostname, port);
        client.setLogLevel(this.outputlevel,this.logLevel);
        client.connect();
    }

    /**
     * Main entry point for the echo server application. 
     * @param args contains the port number at args[0].
     */
    public static void main(String[] args) {
        kv_out.enableLog("logs/client.log", Level.OFF);
        KVClient app = new KVClient(System.in);
        app.run();
        System.out.println("Exiting client main");
    }
}
