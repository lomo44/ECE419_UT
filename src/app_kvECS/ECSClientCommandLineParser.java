package app_kvECS;

import common.command.KVCommandParser;
import app_kvECS.CommandPatterns.*;

public class ECSClientCommandLineParser extends KVCommandParser {
    @Override
    public void initializeCommandPatterns() {
        ECScommandPatterns.add(new KVCommandPatternStart());
        ECScommandPatterns.add(new KVCommandPatternStop());
        ECScommandPatterns.add(new KVCommandPatternShutdown());
        ECScommandPatterns.add(new KVCommandPatternAddNode());
        ECScommandPatterns.add(new KVCommandPatternAddNodes());
        ECScommandPatterns.add(new KVCommandPatternRemoveNode());
    }
}
