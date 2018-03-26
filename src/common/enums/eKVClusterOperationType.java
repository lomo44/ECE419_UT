package common.enums;

public enum  eKVClusterOperationType {
    JOIN(0),
    EXIT(1),
    CREATE(2),
    REMOVE(3);

    private int value;

    eKVClusterOperationType(int value){
        this.value = value;
    }

    public int toInt(){return value;}
    public static eKVClusterOperationType fromInt(int value){
        switch (value){
            case 0: return JOIN;
            case 1: return EXIT;
            case 2: return CREATE;
            case 3: return REMOVE;
        }
        return null;
    }
}
