package common.command;


import app_kvClient.CommandPatterns.*;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.Vector;

/**
 * Command line parser for client CLI
 */
public abstract class KVCommandParser {
    protected Vector<KVCommandPattern> ECScommandPatterns;
    protected Vector<KVCommandPattern> commandPatterns;

    public KVCommandParser(){
        commandPatterns = new Vector<KVCommandPattern>();
        initializeCommandPatterns();
    }

    /**
     * Initialize the command line patterns
     */
    public abstract void initializeCommandPatterns();
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

    /**
     * Print help messages of the ECS command
     */
    public void printECSHelpMessages() {
        for (KVCommandPattern pattern: ECScommandPatterns) {
            System.out.println(pattern.getHelpMessageString());
        }
    }
}
