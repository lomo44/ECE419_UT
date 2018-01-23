package app_kvClient;


import app_kvClient.CommandPatterns.*;
import app_kvClient.Commands.KVCommand;

import java.util.Vector;

// End point for commandline parsing, should initialize all of the patterns available
public class KVCommandParser {
    private Vector<KVCommandPattern> commandPatterns;
    public KVCommandParser(){
        commandPatterns = new Vector<KVCommandPattern>();
        initializeCommandPatterns();
    }
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

    public KVCommand getParsedCommand(String command){
        for (KVCommandPattern pattern: commandPatterns
             ) {
            if(pattern.isMatched(command)){
                return pattern.generateCommand(command);
            }
        }
        return null;
    }
    public void printHelpMessages(){
        for (KVCommandPattern pattern: commandPatterns
             ) {
            System.out.println(pattern.getHelpMessageString());
        }
    }
}
