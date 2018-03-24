package app_kvECS;

import common.command.KVCommandParser;
import app_kvECS.CommandPatterns.*;

public class ECSClientCommandLineParser extends KVCommandParser {
    @Override
    public void initializeCommandPatterns() {
        commandPatterns.add(new KVCommandPatternStart());
        commandPatterns.add(new KVCommandPatternStop());
        commandPatterns.add(new KVCommandPatternShutdown());
        commandPatterns.add(new KVCommandPatternAddNode());
        commandPatterns.add(new KVCommandPatternAddNodes());
        //commandPatterns.add(new KVCommandPatternRemoveNodeByIndex());
        commandPatterns.add(new KVCommandPatternGetNodeByKey());
        commandPatterns.add(new KVCommandPatternGetNodes());
        commandPatterns.add(new KVCommandPatternSetRemoteExecutablePath());
        commandPatterns.add(new KvCommandPatternRemoveNodeByName());
        /**
         * Currently, we will not allow user to create cluster. The cluster will be created
         * along with the nodes
         */
//        commandPatterns.add(new KVCommandPatternCreateCluster());
//        commandPatterns.add(new KVCommandPatternRemoveCluster());
        commandPatterns.add(new KVCommandPatternLeaveCluster());
        commandPatterns.add(new KVCommandPatternJoinCluster());
    }
}
