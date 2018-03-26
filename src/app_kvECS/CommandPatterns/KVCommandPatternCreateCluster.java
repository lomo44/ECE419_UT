package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandModifyCluster;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import common.enums.eKVClusterOperationType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternCreateCluster extends KVCommandPattern{
    @Override
    public KVCommand generateCommand(String input) {
        Matcher match = commandRegex.matcher(input);
        KVCommandModifyCluster ret = null;
        if(match.find()){
            ret = new KVCommandModifyCluster(KVCommandType.CREATE_CLUSTER);
            ret.setClusterName(match.group(1));
            ret.setClusterOperationType(eKVClusterOperationType.CREATE);
        }
        return ret;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^createCluster (.*)$");
    }

    @Override
    public String getHelpMessageString() {
        return String.format("createCluster <ClusterName>\n" +
                               "    create a cluster named <ClusterName>");
    }
}
