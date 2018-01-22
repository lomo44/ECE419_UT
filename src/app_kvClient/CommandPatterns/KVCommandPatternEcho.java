package app_kvClient.CommandPatterns;

import app_kvClient.Commands.KVCommand;
import app_kvClient.Commands.KVCommandEcho;

import java.util.regex.Pattern;

public class KVCommandPatternEcho extends KVCommandPattern{
    @Override
    public KVCommand generateCommand(String input) {
        return new KVCommandEcho();
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^echo$");
    }

    @Override
    public String getHelpMessageString() {
        return "Description: Send an echo command to server";
    }
}
