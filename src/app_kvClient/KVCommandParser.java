package app_kvClient;


import app_kvClient.CommandPatterns.*;
import app_kvClient.Commands.KVCommand;

import java.util.Vector;

/**
 * Command line parser for client CLI
 */
public class KVCommandParser {
    private Vector<KVCommandPattern> commandPatterns;

    public KVCommandParser(){
        commandPatterns = new Vector<KVCommandPattern>();
        initializeCommandPatterns();
    }

    /**
     * Initialize the command line patterns
     */
    public void initializeCommandPatterns(){
        commandPatterns.add(new KVCommandPatternConnect());
        commandPatterns.add(new KVCommandPatternDisconnect());
        commandPatterns.add(new KVCommandPatternGet());
        commandPatterns.add(new KVCommandPatternHelp());
        commandPatterns.add(new KVCommandPatternLogLevel());
        commandPatterns.add(new KVCommandPatternPut());
        commandPatterns.add(new KVCommandPatternQuit());
        commandPatterns.add(new KVCommandPatternEcho());
    }

    /**
     * Retrieved a parsed command object based on a given command input
     * @param command string input
     * @return KVCommand if command is recognized, null if not.
     */
    public KVCommand getParsedCommand(String command){
        for (KVCommandPattern pattern: commandPatterns
             ) {
            if(pattern.isMatched(command)){
                return pattern.generateCommand(command);
            }
        }
        return null;
    }

    /**
     * Print help messages of the command
     */
    public void printHelpMessages(){
        for (KVCommandPattern pattern: commandPatterns
             ) {
            System.out.println(pattern.getHelpMessageString());
        }
    }
}
