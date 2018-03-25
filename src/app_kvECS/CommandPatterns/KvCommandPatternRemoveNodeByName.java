package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandRemoveNodeByName;
import common.command.KVCommand;
import common.command.KVCommandPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KvCommandPatternRemoveNodeByName extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        KVCommandRemoveNodeByName ret = null;
        Matcher match = this.commandRegex.matcher(input);
        if(match.find()){
            ret = new KVCommandRemoveNodeByName();
            ret.setNodeName(match.group(1));
        }
        return ret;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^removeNode (.*)$");
    }

    @Override
    public String getHelpMessageString() {
        return String.format("removeNode <NodeName>\n" +
                               "    remove the node/cluster by name\n");
    }
}
