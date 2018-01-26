package app_kvClient;

import java.io.BufferedReader;
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
import client.KVCommInterface;
import common.messages.KVMessage;

public class KVClient implements IKVClient,Runnable {

    private static Logger logger = Logger.getRootLogger();
    private static final String PROMPT = "Client> ";
    private KVStore client = null;
    private boolean stop = false;
    private KVCommandParser cmdParser = new KVCommandParser();
    private Scanner keyboard;
    private KVClientAttribute attribute = new KVClientAttribute();
    private KVOut kv_out = new KVOut();
    private eKVLogLevel outputlevel;
    private eKVLogLevel logLevel;

    public KVClient(InputStream inputStream){
        keyboard = new Scanner(inputStream);
        setLogLevel(eKVLogLevel.OFF,eKVLogLevel.DEBUG);
    }
    public KVClient(){
        keyboard = new Scanner(System.in);
        setLogLevel(eKVLogLevel.OFF,eKVLogLevel.DEBUG);
    }
    @Override
    public void run() {
        kv_out.println_debug("Client Started");
        while (!stop) {
            System.out.print(PROMPT);
            KVCommand cmdInstance = cmdParser.getParsedCommand(keyboard.nextLine());
            if(cmdInstance!=null){
                // Command line correctly parsed
                cmdInstance.handleResponse(executeCommand(cmdInstance));
            }
            else{
                printHelp();
            }
        }
        kv_out.println_debug("Client Stopped");
    }
    public void stop() throws IOException {
        disconnect();
        stop = true;
        kv_out.println_debug("Try to stop client");
    }
    public void disconnect() throws IOException {
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }
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
	public void printHelp() {
        cmdParser.printHelpMessages();
	}
    public KVClientAttribute getAttribute() {
        return attribute;
    }

    @Override
    public void newConnection(String hostname, int port) throws Exception {
        client = new KVStore(hostname, port);
        client.setLogLevel(this.outputlevel,this.logLevel);
        client.connect();
    }

    public KVStore getStore(){
        return client;
    }
    public KVJSONMessage executeCommand(KVCommand cmdInstance){
        return cmdInstance.execute(this);
    }
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
     * Main entry point for the echo server application. 
     * @param args contains the port number at args[0].
     */
    public static void main(String[] args) {
        try {
            new LogSetup("logs/client.log", Level.OFF);
            KVClient app = new KVClient(System.in);
            app.run();
        } catch (IOException e) {
            System.out.println("Error! Unable to initialize logger!");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Exiting client main");
    }
}
