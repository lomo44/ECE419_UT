package app_kvECS.CommandPatterns;

import common.command.KVCommand;
import app_kvECS.Commands.KVCommandRemoveNodeByIndex;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternRemoveNodeByIndex extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandRemoveNodeByIndex newcommand = new KVCommandRemoveNodeByIndex();
        newcommand.setIndex(matcher.group(1));
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^removeNode (\\d*)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "removeNode <index of server>\n" +
                "   Description: Remove a specified server from the application through its index\n";
    }
}
