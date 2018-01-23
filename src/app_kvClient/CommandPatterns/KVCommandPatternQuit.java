package app_kvClient.CommandPatterns;

import app_kvClient.Commands.KVCommand;
import app_kvClient.Commands.KVCommandQuit;

import java.util.regex.Pattern;

public class KVCommandPatternQuit extends KVCommandPattern{
    @Override
    public KVCommand generateCommand(String input) {
        return new KVCommandQuit();
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("quit");
    }

    @Override
    public String getHelpMessageString() {
        return  "quit \n" +
                "   Description: Tears down the active connection to the server and exits the program.\n";
    }
}
