package app_kvECS.CommandPatterns;

import app_kvECS.Commands.KVCommandModifyClusterNode;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import common.enums.eKVClusterOperationType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVCommandPatternJoinCluster extends KVCommandPattern {
    @Override
    public KVCommand generateCommand(String input) {
        Matcher match = this.commandRegex.matcher(input);
        KVCommandModifyClusterNode ret = null;
        if(match.find()){
            ret = new KVCommandModifyClusterNode(KVCommandType.JOIN_CLUSTER);
            ret.setNodeName(match.group(1));
            ret.setClusterName(match.group(2));
            ret.setClusterOperation(eKVClusterOperationType.JOIN);
        }
        return ret;
    }

    @Override
    public Pattern generateRegex() {
        return Pattern.compile("^joinCluster (.*) (.*)$");
    }

    @Override
    public String getHelpMessageString() {
        return String.format("joinCluster <NodeName> <ClusterName>\n" +
                "    Force the node named <NodeName> to join the cluster named <ClusterName>");
    }
}
