package ecs;

import common.networknode.KVStorageNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ECSNode implements IECSNode {
    protected static final Pattern re_pattern = Pattern.compile("(.*) (\\d*)");
    private KVStorageNode node;

    public ECSNode(KVStorageNode node) {
        this.node = node;
    }

    public static ECSNode fromString(String str) {
        Matcher match = re_pattern.matcher(str);
        if (match.matches()) {
            return new ECSNode(new KVStorageNode(match.group(1),Integer.parseInt(match.group(2))));
        }
        return null;
    }

    /**
     * Convert ID into string representation
     * @return
     */
    public String toString(){
        return node.toString();
    }

    /**
    * @return  the name of the node (ie "Server 8.8.8.8")
    */
    @Override
    public String getNodeName() {
        return node.getserverName();
    }

    /**
     * @return  the hostname of the node (ie "8.8.8.8")
     */
    @Override
    public String getNodeHost() {
        return node.getHostName();
    }

    /**
     * @return  the port number of the node (ie 8080)
     */
    @Override
    public int getNodePort() {
        return node.getPortNumber();
    }

    /**
     * @return  array of two strings representing the low and high range of the hashes that the given node is responsible for
     */
    @Override
    public String[] getNodeHashRange() {
        return node.getHashRange().getHashRangeString();
    }
}
