package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandShutdown;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternShutdown extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandShutdown newcommand = new KVCommandShutdown();
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^shutDown(\\s*)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "shutDown\n" +
                "   Description: Shut down all live servers & exit the application.\n";
    }
}
