package app_kvClient.CommandPatterns;

import common.command.KVCommand;
import app_kvClient.Commands.KVCommandConnect;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternConnect extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher matcher = commandRegex.matcher(input);
        matcher.find();
        KVCommandConnect newcommand = new KVCommandConnect();
        newcommand.setHostName(matcher.group(1));
        newcommand.setPort(matcher.group(2));
        return newcommand;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^connect (\\S*) (\\d*)$");
    }

    @Override
    public String getHelpMessageString() {
        return  "connect <address> <port> \n" +
                "   Description: Establish a TCP-connection to the storage \n" +
                "       server based on the given server address \n" +
                "       and the port number of the storage service. \n" +
                "   Usage: \n" +
                "       address: Hostname or IP address of the storage server.\n" +
                "       port:  The port of the storage service on the respective server.\n";
    }
}
