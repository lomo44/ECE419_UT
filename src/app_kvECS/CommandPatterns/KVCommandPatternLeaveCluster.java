package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandModifyClusterNode;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import common.enums.eKVClusterOperationType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternLeaveCluster extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher match = this.commandRegex.matcher(input);
        KVCommandModifyClusterNode ret = null;
        if(match.find()){
            ret = new KVCommandModifyClusterNode(KVCommandType.LEAVE_CLUSTER);
            ret.setNodeName(match.group(1));
            ret.setClusterName(match.group(2));
            ret.setClusterOperation(eKVClusterOperationType.EXIT);
        }
        return ret;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^leaveCluster (.*) (.*)$");
    }

    @Override
    public String getHelpMessageString() {
        return String.format("leaveCluster <NodeName> <ClusterName>\n" +
                               "    Force the node named <NodeName> to leave the cluster named <ClusterName>");
    }
}
