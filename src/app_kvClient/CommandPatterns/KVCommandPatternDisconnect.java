package app_kvClient.CommandPatterns;

import common.command.KVCommand;
import app_kvClient.Commands.KVCommandDisconnect;
import common.command.KVCommandPattern;

import java.util.regex.Pattern;

public class KVCommandPatternDisconnect extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        return new KVCommandDisconnect();
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^disconnect$");
    }
    @Override
    public String getHelpMessageString() {
        return  "disconnect \n" +
                "   Description: ETries to disconnect from the connected server.\n";
    }
}
