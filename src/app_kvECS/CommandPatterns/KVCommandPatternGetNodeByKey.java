package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandGetNodeByKey;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternGetNodeByKey extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandGetNodeByKey newcommand = new KVCommandGetNodeByKey();
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^getNodeByKey (\\S+)$");
    }

    @Override
    public String getHelpMessageString() {
        return "getNodeByKey\n" +
                "   Description: Returns the IECSNode responsible for the given key\n";
    }
}
