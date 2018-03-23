package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandAddNodes;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternAddNode extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandAddNodes newcommand = new KVCommandAddNodes();
        newcommand.setNumNodes(1);
        newcommand.setCacheSize(Integer.parseInt(matcher.group(1)));
        newcommand.setCacheStrategy(matcher.group(2));
        newcommand.setClusterName(matcher.group(3));
        return newcommand;
    }

    @Override
    public Pattern generateRegex() { return Pattern.compile("^addNode (\\d*) (FIFO|LRU|LFU) ?(.*)$"); }

    @Override
    public String getHelpMessageString() {
        return  "addNode cacheSize FIFO|LRU|LFU [clusterName]\n" +
                "    Description: Start up a new server,\n" +
                "    with the specified cache size & replacement strategy\n";
    }
}
