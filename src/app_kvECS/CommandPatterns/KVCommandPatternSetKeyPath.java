package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandSetKeyPath;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternSetKeyPath extends KVCommandPattern{

	@Override
	public KVCommand generateCommand(String input) {
		Matcher match = this.commandRegex.matcher(input);
        match.find();
        return new KVCommandSetKeyPath(match.group(1));
	}

	@Override
	public Pattern generateRegex() {
		return Pattern.compile("^setKeyPath (.*)$");
	}

	@Override
	public String getHelpMessageString() {
		return "setKeyPath <path>: \n" +
                "   set the private key path for ssh keyless connection\n";
	}
	
}
