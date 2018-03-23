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
        newcommand.setNumNodes(Integer.parseInt(matcher.group(1)));
        newcommand.setCacheSize(Integer.parseInt(matcher.group(2)));
        newcommand.setCacheStrategy(matcher.group(3));
        newcommand.setClusterName(matcher.group(4));
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^addNodes (\\d*) (\\d*) (FIFO|LRU|LFU) ?(.*)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "addNodes numberOfNodes cacheSize FIFO|LRU|LFU [clusterName]\n" +
                "   Description: Start up the specified number of servers,\n" +
                "   with the specified cache size & replacement strategy\n";
    }
}
