package common.enums;

public enum  eKVClusterOperationType {
    JOIN(0),
    EXIT(1);

    private int value;

    eKVClusterOperationType(int value){
        this.value = value;
    }

    public int toInt(){return value;}
    public static eKVClusterOperationType fromInt(int value){
        switch (value){
            case 0: return JOIN;
            case 1: return EXIT;
        }
        return null;
    }
}
