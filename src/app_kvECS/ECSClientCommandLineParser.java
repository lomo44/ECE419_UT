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
        commandPatterns.add(new KVCommandPatternRemoveNode());
        commandPatterns.add(new KVCommandPatternGetNodeByKey());
        commandPatterns.add(new KVCommandPatternGetNodes());
    }
}
