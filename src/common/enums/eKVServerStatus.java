package common.enums;

public enum eKVServerStatus {
    STOPPED(0),
    STARTED(1),
    MIGRATING(2);

    private int value;
    eKVServerStatus(int value){this.value = value;}
    public int toInt(){return value;}
    public static eKVServerStatus fromInt(int value){
        switch (value){
            case 0: return STOPPED;
            case 1: return STARTED;
            case 2: return MIGRATING;
        }
        return null;
    }
}