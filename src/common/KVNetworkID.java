package common;

import common.datastructure.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KVNetworkID {
    private Pair<String, Integer> id = new Pair<>("",0);
    public static final Pattern re_pattern = Pattern.compile("(.*)@(\\d*)");
    public KVNetworkID(String hostname, int portNumber){
        id.x = hostname;
        id.y = portNumber;
    }
    public String toString(){
        return id.x +'@'+ Integer.toString(id.y);
    }
    public static KVNetworkID fromString(String str){
        Matcher match = re_pattern.matcher(str);
        if(match.matches()){
            return new KVNetworkID(match.group(1),Integer.parseInt(match.group(2)));
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        KVNetworkID rhs = (KVNetworkID) o;
        return this.id.x.matches(rhs.id.x ) && this.id.y == rhs.id.y;
    }
}
