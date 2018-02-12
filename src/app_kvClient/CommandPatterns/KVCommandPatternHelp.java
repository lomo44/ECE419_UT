package app_kvClient.CommandPatterns;

import common.command.KVCommand;
import app_kvClient.Commands.KVCommandHelp;
import common.command.KVCommandPattern;

import java.util.regex.Pattern;

public class KVCommandPatternHelp extends KVCommandPattern {
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
