package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandSetServerJarPath;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternSetRemoteExecutablePath extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher match = this.commandRegex.matcher(input);
        match.find();
        return new KVCommandSetServerJarPath(match.group(1));
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^setServerJarPath (.*)$");
    }

    @Override
    public String getHelpMessageString() {
        return "setServerJarPath <path>: \n" +
                "   set the executable path to the server jar file on the remote\n";
    }
}
