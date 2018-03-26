package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandModifyCluster;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import common.enums.eKVClusterOperationType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternRemoveCluster extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher match = commandRegex.matcher(input);
        KVCommandModifyCluster ret = null;
        if(match.find()){
            ret = new KVCommandModifyCluster(KVCommandType.REMOVE_CLUSTER);
            ret.setClusterName(match.group(1));
            ret.setClusterOperationType(eKVClusterOperationType.REMOVE);
        }
        return ret;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^removeCluster (.*)$");
    }

    @Override
    public String getHelpMessageString() {
        return String.format("removeCluster <ClusterName>\n" +
                "    remove a cluster named <ClusterName>");
    }
}
