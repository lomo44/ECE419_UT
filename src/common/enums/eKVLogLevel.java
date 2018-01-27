package common.enums;

public enum eKVLogLevel {
    OFF(6),
    INFO(5),
    WARN(4),
    ERROR(3),
    FATAL(2),
    DEBUG(1),
    ALL(0);

    private int value;
    private eKVLogLevel(int value){
        this.value = value;
    }

    /**
     * Cast log level to integer implementation
     * @return
     */
    public int toInt(){
        return value;
    }

    /**
     * Cast an int implementation to loglevel
     * @param value
     * @return
     */
    public static eKVLogLevel fromInt(int value){
        switch (value){
            case 0: return ALL;
            case 1: return DEBUG;
            case 2: return FATAL;
            case 3: return ERROR;
            case 4: return WARN;
            case 5: return INFO;
            case 6: return OFF;
        }
        return null;
    }
}
