package app_kvECS.CommandPatterns;

import common.command.KVCommand;
import app_kvECS.Commands.KVCommandRemoveNode;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternRemoveNode extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandRemoveNode newcommand = new KVCommandRemoveNode();
        newcommand.setIndex(matcher.group(1));
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^removeNode (\\S+)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "removeNode <index of server>\n" +
                "   Description: Remove a specified server from the application through its index\n";
    }
}
