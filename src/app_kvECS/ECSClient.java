package app_kvECS;

import java.util.Map;
import java.util.Collection;
import java.util.Scanner;

import common.command.KVCommandParser;
import common.command.KVCommand;

import ecs.IECSNode;

public class ECSClient implements IECSClient {

    private boolean stop = false;
    private static final String PROMPT = "ECSClient>";
    private KVCommandParser cmdParser = new ECSClientCommandLineParser();
    private Scanner keyboard;

    @Override
    public boolean start() {
        // TODO
        return false;
    }

    @Override
    public boolean stop() {
        // TODO
        return false;
    }

    @Override
    public boolean shutdown() {
        // TODO
        return false;
    }

    @Override
    public IECSNode addNode(String cacheStrategy, int cacheSize) {
        // TODO
        return null;
    }

    @Override
    public Collection<IECSNode> addNodes(int count, String cacheStrategy, int cacheSize) {
        // TODO
        return null;
    }

    @Override
    public Collection<IECSNode> setupNodes(int count, String cacheStrategy, int cacheSize) {
        // TODO
        return null;
    }

    @Override
    public boolean awaitNodes(int count, int timeout) throws Exception {
        // TODO
        return false;
    }

    @Override
    public boolean removeNodes(Collection<String> nodeNames) {
        // TODO
        return false;
    }

    @Override
    public Map<String, IECSNode> getNodes() {
        // TODO
        return null;
    }

    @Override
    public IECSNode getNodeByKey(String Key) {
        // TODO
        return null;
    }

    /**
     * Run ECSClient
     */
    public void run() {
        while (!stop) {
            System.out.print(PROMPT);
            KVCommand<ECSClient> cmdInstance = cmdParser.getParsedCommand(keyboard.nextLine());
            if (cmdInstance != null) {
                executeCommand(cmdInstance);
            } else {
                printHelp();
            }
        }
    }

    public void executeCommand(KVCommand cmdInstance) {
        cmdInstance.execute(this);
    }

    public static void main(String[] args) {
        // TODO
    }
}
