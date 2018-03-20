package common.enums;

public enum eKVNetworkNodeType {
    NETWORK_NODE(1),
    STORAGE_NODE(2),
    STORAGE_CLUSTER(3),
    UNKNOWN(0);

    private int value;

    eKVNetworkNodeType(int value){
        this.value = value;
    }

    public int toInt(){return value;}
    public static eKVNetworkNodeType fromInt(int value){
        switch (value){
            case 0: return UNKNOWN;
            case 1: return NETWORK_NODE;
            case 2: return STORAGE_NODE;
            case 3: return STORAGE_CLUSTER;
        }
        return null;
    }
}
