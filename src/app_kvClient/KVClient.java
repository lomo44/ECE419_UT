package app_kvClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.IllegalArgumentException;
import java.net.UnknownHostException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import logger.LogSetup;

import client.KVStore;
import client.KVCommInterface;
import common.messages.KVMessage;

public class KVClient implements IKVClient {

    private static Logger logger = Logger.getRootLogger();
    private static final String PROMPT = "Client> ";
    private BufferedReader stdin;
    private KVStore client = null;
    private boolean stop = false;

    public void run() {
        while (!stop) {
            stdin = new BufferedReader(new InputStreamReader(System.in));
            System.out.print(PROMPT);

            try {
                String cmdLine = stdin.readLine();
                this.handleCommand(cmdLine);
            } catch (IOException e) {
                stop = true;
                printError("CLI does not respond - Application terminated ");
            }
        }
    }

    private void handleCommand(String cmdLine) {
        String[] tokens = cmdLine.split("\\s+");

        if (tokens[0].equals("connect")) {
            if (tokens.length == 3) {
                try {
                    String address = tokens[1];
                    int port = Integer.parseInt(tokens[2]);
                    newConnection(address, port);
                } catch (Exception e) {
                    printError("Could not establish connection!");
                    logger.warn("Could not establish connection!", e);
                }
            } else {
                printError("Invalud number of parameters!");
            }

        } else if (tokens[0].equals("disconnect")) {
            disconnect();

        } else if (tokens[0].equals("put")) {
            if (tokens.length == 3) {
                if (client != null && client.isRunning()) {
                    try {
                        String key = tokens[1];
                        String value = tokens[2];
                        put(key, value);
                    } catch (Exception e) {
                        printError("No valid key|value!");
                        logger.info("No valid key|value!", e);
                    }
                } else {
                    printError("Not connected!");
                }
            } else {
                printError("Invalid number of parameters!");
            }
        } else if (tokens[0].equals("get")) {
            if (tokens.length == 1) {
                if (client != null && client.isRunning()) {
                    try {
                        String key = tokens[1];
                        get(key);
                    } catch (Exception e) {
                        printError("No valid key!");
                        logger.info("No valid key!", e);
                    }
                }
            } else {
                printError("Invalid number of parameters!");
            }
        } else if (tokens[0].equals("logLevel")) {
            if (tokens.length == 2) {
                String level = setLevel(tokens[1]);
                if (level.equals(LogSetup.UNKNOWN_LEVEL)) {
                    printError("No valid log level!");
                    printPossibleLogLevels();
                } else {
                    System.out.println(PROMPT + "Log level changed to level " + level);
                }
            } else {
                printError("Invalid number of parameters!");
            }
        } else if (tokens[0].equals("help")) {
            printHelp();
        } else if (tokens[0].equals("quit")) {
            stop = true;
            disconnect();
            System.out.println(PROMPT + "Application exit!");
            
        } else {
            printError("Unknown command");
            printHelp();
        }
    }

    private void handleMessage(KVMessage msg) {
        if (client != null && client.isRunning()) {
            String str = client.handleMessage(msg);
            System.out.println(str);
            System.out.print(PROMPT);
        }
    }

    private void printError(String error) {
        System.out.println(PROMPT + "Error! " + error);
    }

    private void disconnect() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    private void put(String key, String value) throws Exception {
        KVMessage incoming_msg = client.put(key, value);
        handleMessage(incoming_msg);
    }

    private void get(String key) throws Exception {
        KVMessage incoming_msg = client.get(key);
        handleMessage(incoming_msg);
    }

    private String setLevel(String levelString) {
		
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

	private void printPossibleLogLevels() {
		System.out.println(PROMPT 
				+ "Possible log levels are:");
		System.out.println(PROMPT 
				+ "ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF");
	}

	private void printHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append(PROMPT).append("ECHO CLIENT HELP (Usage):\n");
		sb.append(PROMPT);
		sb.append("::::::::::::::::::::::::::::::::");
		sb.append("::::::::::::::::::::::::::::::::\n");
		sb.append(PROMPT).append("connect <host> <port>");
		sb.append("\t establishes a connection to a server\n");
		sb.append(PROMPT).append("send <text message>");
		sb.append("\t\t sends a text message to the server \n");
		sb.append(PROMPT).append("disconnect");
		sb.append("\t\t\t disconnects from the server \n");
		
		sb.append(PROMPT).append("logLevel");
		sb.append("\t\t\t changes the logLevel \n");
		sb.append(PROMPT).append("\t\t\t\t ");
		sb.append("ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF \n");
		
		sb.append(PROMPT).append("quit ");
		sb.append("\t\t\t exits the program");
		System.out.println(sb.toString());
	}

    @Override
    public void newConnection(String hostname, int port) throws Exception {
        client = new KVStore(hostname, port);
        client.connect();
        client.addListener(this);
        // client.start();
    }

    @Override
    public KVCommInterface getStore(){
        return client;
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
