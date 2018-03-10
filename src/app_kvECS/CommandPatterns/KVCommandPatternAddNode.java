package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandAddNode;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternAddNode extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandAddNode newcommand = new KVCommandAddNode();
        newcommand.setPortNumber(Integer.valueOf(matcher.group(1)));
        newcommand.setCacheSize(Integer.valueOf(matcher.group(2)));
        newcommand.setCacheStrategy(matcher.group(3));
        return newcommand;
    }

    @Override
    public Pattern generateRegex() { return Pattern.compile("^addNode (\\d*) (\\d*) (FIFO|LRU|LFU)$"); }

    @Override
    public String getHelpMessageString() {
        return  "addNode <cache size> <cache strategy>\n" +
                "   Description: Start up a new server,\n" +
        "   with the specified cache size & replacement strategy\n";
    }
}
