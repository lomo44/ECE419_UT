package common.enums;

import app_kvServer.IKVServer;

public enum eKVExtendCacheType{
    None("None"), LRU("LRU"), LFU("LFU"), FIFO("FIFO");
    private String str;

    private eKVExtendCacheType(String str) {
        this.str = str;
    }

    public String toString() {
        return str;
    }

    public IKVServer.CacheStrategy toCacheStrategy(){
        switch (str){
            case "FIFO": return IKVServer.CacheStrategy.FIFO;
            case "LFU": return IKVServer.CacheStrategy.LFU;
            case "LRU": return IKVServer.CacheStrategy.LRU;
        }
        return null;
    }

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