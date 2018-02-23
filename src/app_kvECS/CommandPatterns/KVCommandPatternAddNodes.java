package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandAddNodes;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternAddNodes extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandAddNodes newcommand = new KVCommandAddNodes();
        newcommand.setNumNodes(matcher.group(1));
        newcommand.setCacheSize(matcher.group(2));
        newcommand.setCacheStrategy(matcher.group(3));
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^addNodes (\\d*) (\\d*) (\\S*)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "addNodes <number of nodes> <cache size> <cache strategy>\n" +
                "   Description: Start up the specified number of servers,\n"
                "   with the specified cache size & replacement strategy\n";
    }
}
