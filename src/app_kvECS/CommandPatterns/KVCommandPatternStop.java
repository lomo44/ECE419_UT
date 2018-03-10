package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandStop;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternStop extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandStop newcommand = new KVCommandStop();
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^stop(\\s*)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "stop\n" +
                "   Description: Stop all live servers so that all client requests are rejected.\n";
    }
}
