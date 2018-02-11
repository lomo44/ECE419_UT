package app_kvClient.CommandPatterns;

import app_kvClient.Commands.KVCommand;
import app_kvClient.Commands.KVCommandGet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternGet extends KVCommandPattern{
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandGet newcommand = new KVCommandGet();
        newcommand.setKey(matcher.group(1));
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^get (\\S*)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "getValue <key> \n" +
                "   Description: Retrieves the value for the given key from the storage server.\n"+
                "   Usage: \n" +
                "       key: the key that indexes the desired value (max length 20 Bytes)\n";
    }
}
