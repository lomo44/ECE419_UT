package app_kvClient;

import app_kvClient.CommandPatterns.*;
import common.command.KVCommandParser;

public class KVClientCommandLineParser extends KVCommandParser {
    @Override
    public void initializeCommandPatterns() {
        commandPatterns.add(new KVCommandPatternConnect());
        commandPatterns.add(new KVCommandPatternDisconnect());
        commandPatterns.add(new KVCommandPatternGet());
        commandPatterns.add(new KVCommandPatternHelp());
        commandPatterns.add(new KVCommandPatternLogLevel());
        commandPatterns.add(new KVCommandPatternPut());
        commandPatterns.add(new KVCommandPatternQuit());
        //commandPatterns.add(new KVCommandPatternEcho());
    }
}
