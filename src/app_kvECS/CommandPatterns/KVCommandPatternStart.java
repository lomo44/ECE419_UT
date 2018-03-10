package app_kvECS.CommandPatterns;

import common.command.KVCommand;
import app_kvECS.Commands.KVCommandStart;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternStart extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandStart newcommand = new KVCommandStart();
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^start(\\s*)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "start\n" +
                "   Description: Start all live servers so that all client requests are processed.\n";
    }
}
