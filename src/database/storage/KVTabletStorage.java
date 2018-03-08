package database.storage;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Storage class that used partitioned disk file.
 * Note: All of the read/write operations are synchronized, thus try to avoid excessive access of thie storage class.
 */
public class KVTabletStorage implements KVStorage {
    private HashMap<UUID,Set<String>> tabletKeyMap = new HashMap<>();
    private Queue<KVTablet> writableTabletQueue = new PriorityQueue<>(10, new Comparator<KVTablet>() {
        @Override
        public int compare(KVTablet tablet, KVTablet t1) {
            if(tablet.getEntryCount() < t1.getEntryCount()){
                return 1;
            }
            else if(tablet.getEntryCount() == t1.getEntryCount()){
                return 0;
            }
            else{
                return -1;
            }
        }
    });
    private String tabletStoragePath;
    private int maxEntryCounterPerTablet = 1200;

    /**
     * Create a tablet storage instance. Upon creation, the constructor will search in the
     * tablet storage directory and look for any pre-existing tablets. If there are any
     * pre-existing tablets, this constructor will try to load each tablets and consolidate
     * keys in to a map for future searching
     * @param tabletStoragePath tablet storage path
     */
    public KVTabletStorage(String tabletStoragePath, int maxEntryCounterPerTablet){
        initializeTablets(tabletStoragePath);
        this.tabletStoragePath = tabletStoragePath;
        this.maxEntryCounterPerTablet = maxEntryCounterPerTablet;
    }

    @Override
    public synchronized boolean inStorage(String key) {
        return getResponsibleTabletID(key) != null;
    }
    @Override
    public synchronized void clearStorage(){
        for (UUID id: tabletKeyMap.keySet()
             ) {
            KVTablet tablet = KVTablet.load(tabletStoragePath,id);
            tablet.clear();
        }
    }
    @Override
    public synchronized String getFromStorage(String key) throws Exception {
        UUID id = getResponsibleTabletID(key);
        if(id!=null){
            KVTablet tablet = KVTablet.load(tabletStoragePath,id);
            if(tablet!=null){
                return tablet.getFromStorage(key);
            }
        }
        throw new Exception();
    }
    @Override
    public synchronized void putToStorage(String key, String value) throws IOException{
        UUID id = getResponsibleTabletID(key);
        if(id!=null){
            // Update
            KVTablet table = KVTablet.load(tabletStoragePath,id);
            table.putToStorage(key,value);
        }
        else {
            // New entry
            KVTablet top = writableTabletQueue.peek();
            if (top != null) {
                top.putToStorage(key, value);
                if (top.getEntryCount() >= maxEntryCounterPerTablet) {
                    writableTabletQueue.remove();
                }
                // update key
            }
            else {
                // need a new tablet
                KVTablet newTablet = KVTablet.createNewTablet(tabletStoragePath);
                newTablet.putToStorage(key,value);
                writableTabletQueue.add(newTablet);
                tabletKeyMap.put(newTablet.getID(),newTablet.getKeys());
            }
        }
    }

    @Override
    public Set<String> getKeys() {
        Set<String> keys = new HashSet<>();
        for (UUID id: tabletKeyMap.keySet()
             ) {
            keys.addAll(tabletKeyMap.get(id));
        }
        return keys;
    }

    public void setMaxEntryCounterPerTablet(int maxEntryCounterPerTablet) {
        this.maxEntryCounterPerTablet = maxEntryCounterPerTablet;
    }

    /**
     * Check if the tablet is full
     * @param tablet tablet instance
     * @return True if it is less than the maxEntryCounterPerTablet defined in the constructor, false if not
     */
    private boolean isTabletFull(KVTablet tablet){
        return tablet.getEntryCount() < maxEntryCounterPerTablet;
    }

    /**
     * Initialize the tablets
     * @param storagePath tablets storage path
     */
    private void initializeTablets(String storagePath){
        File storageDir = new File(storagePath);
        // Load the keys
        if(storageDir.exists() && storageDir.isDirectory()){
            File[] tablets = storageDir.listFiles();
            for(File tablet : tablets){
                try {
                    // Only load the keys
                    KVTablet newTablet = KVTablet.load(tablet);
                    if(newTablet!=null){
                        tabletKeyMap.put(newTablet.getID(),newTablet.getKeys());
                        if(isTabletFull(newTablet)){
                            writableTabletQueue.add(newTablet);
                        }
                    }
                } catch (Exception e) {
                    // Invalid tablet, ignored.
                }
            }
        }
    }

    /**
     * Using a key, find it's corresponding tablet's UUID
     * @param key key string
     * @return UUID instance if found, null if not.
     */
    private UUID getResponsibleTabletID(String key){
        for (UUID id: tabletKeyMap.keySet()
                ) {
            if(tabletKeyMap.get(id).contains(key)){
                return id;
            }
        }
        return null;
    }
}
