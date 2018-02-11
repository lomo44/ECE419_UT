package common.enums;

import app_kvServer.IKVServer;

public enum eKVExtendCacheType{
    None("None"), LRU("LRU"), LFU("LFU"), FIFO("FIFO");
    private String str;

    private eKVExtendCacheType(String str) {
        this.str = str;
    }

    /**
     * Cast to string
     * @return
     */
    public String toString() {
        return str;
    }

    /**
     * Cast the extend cache stype to cache strategy
     * @return IKVServer.CacheStrategy
     */
    public IKVServer.CacheStrategy toCacheStrategy(){
        switch (str){
            case "FIFO": return IKVServer.CacheStrategy.FIFO;
            case "LFU": return IKVServer.CacheStrategy.LFU;
            case "LRU": return IKVServer.CacheStrategy.LRU;
        }
        return null;
    }

    /**
     * Create a instance of eKVExtendCacheType based on input string
     * @param str input string
     * @return eKVExtendCacheType
     */
    public static eKVExtendCacheType fromString(String str) {
        switch (str) {
            case "FIFO":
                return FIFO;
            case "LFU":
                return LFU;
            case "LRU":
                return LRU;
            default:
                return None;
        }
    }
};