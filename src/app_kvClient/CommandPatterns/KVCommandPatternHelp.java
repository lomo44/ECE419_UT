package app_kvClient.CommandPatterns;

import app_kvClient.Commands.KVCommand;
import app_kvClient.Commands.KVCommandHelp;

import java.util.regex.Pattern;

public class KVCommandPatternHelp extends KVCommandPattern{
    @Override
    public KVCommand generateCommand(String input) {
        return new KVCommandHelp();
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^help$");
    }

    @Override
    public String getHelpMessageString() {
        return "";
    }
}
