package app_kvClient.CommandPatterns;

import app_kvClient.Commands.KVCommand;
import app_kvClient.Commands.KVCommandLogLevel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternLogLevel extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandLogLevel newcommand = new KVCommandLogLevel();
        newcommand.setLevel(matcher.group(1));
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^loglevel ((?:ALL)|(?:DEBUG)|(?:WARN)|(?:FATAL)|(?:OFF)|(?:INFO)|(?:ERROR))$");
    }

    @Override
    public String getHelpMessageString() {
        return  "logLevel <level> \n" +
                "   Description: Sets the logger to the specified log levelSets the logger to the specified log level.\n"+
                "   Usage: \n" +
                "       level: One of the following log4j log levels:\n" +
                "              (ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF)\n";
    }
}
