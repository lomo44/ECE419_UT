package ecs;

import common.datastructure.Pair;
import common.datastructure.KVRange;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ECSNode implements IECSNode {
    private Pair<String, Integer> id = new Pair<>("",0);
    protected static final Pattern re_pattern = Pattern.compile("(.*) (\\d*)");
    private BigInteger hashKey;
    private KVRange<BigInteger> hashRange;

    public ECSNode(String hostName, int portNumber) {
        id.x = hostName;
        id.y = portNumber;
    }

    public static ECSNode fromString(String str) {
        Matcher match = re_pattern.matcher(str);
        if (match.matches()) {
            return new ECSNode(match.group(1),Integer.parseInt(match.group(2)));
        }
        return null;
    }

    /**
     * Convert ID into string representation
     * @return
     */
    public String toString(){
        return id.x +' '+ Integer.toString(id.y);
    }

    public KVRange<BigInteger> getHashRange() {
        return this.hashRange;
    }

    boolean isResponsible(BigInteger hash) {
        if (hashRange != null) {
            return hashRange.inRange(hash);
        } else {
            return false;
        }
    }

    public void setHashRange(KVRange<BigInteger> hashRange) {
        this.hashRange = hashRange;
    }

    public boolean equals(Object o) {
        ECSNode rhs = (ECSNode) o;
        return this.id.x.matches(rhs.id.x) && this.id.y == rhs.id.y;
    }

    /**
    * @return  the name of the node (ie "Server 8.8.8.8")
    */
    @Override
    public String getNodeName() {
        return hashKey.toString();
    }

    /**
     * @return  the hostname of the node (ie "8.8.8.8")
     */
    @Override
    public String getNodeHost() {
        return id.x;
    }

    /**
     * @return  the port number of the node (ie 8080)
     */
    @Override
    public int getNodePort() {
        return id.y;
    }

    /**
     * @return  array of two strings representing the low and high range of the hashes that the given node is responsible for
     */
    @Override
    public String[] getNodeHashRange() {
        return hashRange.getHashRange();
    }
}
