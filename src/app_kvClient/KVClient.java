package app_kvClient;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.Scanner;

import app_kvClient.Commands.KVCommand;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import logger.LogSetup;

import client.KVStore;
import client.KVCommInterface;
import common.messages.KVMessage;

public class KVClient implements IKVClient {

    private static Logger logger = Logger.getRootLogger();
    private static final String PROMPT = "Client> ";
    private KVStore client = null;
    private boolean stop = false;
    private KVCommandParser cmdParser = new KVCommandParser();
    private Scanner keyboard = new Scanner(System.in);
    public void run() {
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
    }
    public void stop(){
        stop = true;
        disconnect();
    }
    public void disconnect() {
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
    @Override
    public void newConnection(String hostname, int port) throws Exception {
        client = new KVStore(hostname, port);
        client.connect();
    }
    @Override
    public KVCommInterface getStore(){
        return client;
    }

    public KVMessage executeCommand(KVCommand cmdInstance){
        return cmdInstance.execute(this);
    }

    /**
     * Main entry point for the echo server application. 
     * @param args contains the port number at args[0].
     */
    public static void main(String[] args) {
        try {
            new LogSetup("logs/client.log", Level.OFF);
            KVClient app = new KVClient();
            app.run();
        } catch (IOException e) {
            System.out.println("Error! Unable to initialize logger!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
