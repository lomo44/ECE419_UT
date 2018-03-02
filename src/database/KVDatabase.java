package database;

import common.enums.eKVExtendCacheType;
import database.cache.KVFIFOCache;
import database.cache.KVLFUCache;
import database.cache.KVCache;
import database.cache.KVLRUCache;
import database.storage.KVStorage;
import database.storage.KVTabletStorage;
import database.storage.MMStorage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class KVDatabase implements IKVDatabase {

	private int cacheSize;
	private eKVExtendCacheType cacheStrategy;
	private long storageSize;
	private KVCache cache;
	private KVStorage storage;
	private ReadWriteLock writeLock = new ReentrantReadWriteLock();
	
	public KVDatabase (int sizeofCache,long sizeofStorage,String cacheStrat){
		storageSize =sizeofStorage;
		cacheSize = sizeofCache;
		cacheStrategy = eKVExtendCacheType.fromString(cacheStrat);
		storage = new KVTabletStorage("./tmp",1200);
		switch(cacheStrategy) {
			case FIFO:
				cache = new KVFIFOCache(cacheSize);
				break;
			case LRU:
				cache = new KVLRUCache(cacheSize) ;
				break;
			default:case LFU:
				cache = new KVLFUCache(cacheSize);
		}
	}
	


	@Override
	public String getKV(String key) throws Exception {
		String value= null;
		lockRead();
		try {
			value = cache.getFromCache(key);
		} catch (Exception e) {
			try {
				value=storage.getFromStorage(key);
				cache.putToCache(key, value);
				unlockRead();
				return value;
			} catch (Exception e1) {
			    unlockRead();
				throw e1;
			}
		}
		unlockRead();
		return value;
	}

	@Override
	public void putKV(String key, String value) throws Exception {
        lockRead();
	    try {
            cache.putToCache(key, value);
            storage.putToStorage(key, value);
        } catch (Exception e) {
            unlockRead();
            throw e;
        }
        unlockRead();
	}

	public Set<String> getKeys(){
		return storage.getKeys();
	}

	@Override
	public void kill() throws IOException {
		cache.clearCache();
	}

	@Override
	public void close() throws IOException {
		cache.clearCache();
	}

	@Override
	public boolean inStorage(String key) {
		return storage.inStorage(key);
	}

	@Override
	public boolean inCache(String key) {
		return cache.inCache(key);
	}

	@Override
	public void flushCache() {
		cache.flushCache();
	}

	@Override
	public void flushStorage() throws IOException {
		cache.clearCache();
		storage.clearStorage();
	}

	public void lockWrite(){
	    this.writeLock.writeLock().lock();
    }

    public void unlockWrite(){
	    this.writeLock.writeLock().unlock();
    }

    private void lockRead(){
	    this.writeLock.readLock().lock();;
    }
    private void unlockRead(){
        this.writeLock.readLock().unlock();;
    }

}