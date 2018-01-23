package app_kvClient.CommandPatterns;

import app_kvClient.Commands.KVCommand;
import app_kvClient.Commands.KVCommandGet;
import app_kvClient.Commands.KVCommandPut;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternPut extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandPut newcommand = new KVCommandPut();
        newcommand.setKey(matcher.group(1));
        newcommand.setValue(matcher.group(2));
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^put (\\S*) (.*)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "get <key> \n" +
                "   Description: - Inserts a key-value pair into the storage server data structures.\n" +
                "                - Updates (overwrites) the current value with the given value if the server already contains the specified key.\n" +
                "                - Deletes the entry for the given key if <value> equals null.\n"+
                "   Usage: \n" +
                "       key: arbitrary String (max length 20 Bytes)\n" +
                "       value: arbitrary String (max. length 120 kByte)\n";
    }
}
