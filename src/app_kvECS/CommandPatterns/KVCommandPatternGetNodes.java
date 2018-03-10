package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandGetNodes;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternGetNodes extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandGetNodes newcommand = new KVCommandGetNodes();
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^getNodes(\\s*)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "getNodes\n" +
                "   Description: Returns a map of <NodeName, IECSNode>\n";
    }
}
