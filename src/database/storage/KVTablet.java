package database.storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KVTablet{
    private ConcurrentHashMap<String,String> membuffer = null;
    private UUID id;
    private String filepath;

    private KVTablet(){
    }

    /**
     * Check if the key has value inside this tablet?
     * @param key key string
     * @return true if this key has a corresponding value inside this tablet
     */
    public boolean inStorage(String key) {
        return membuffer.contains(key);
    }

    /**
     * Retrieve the value from this tablet using key
     * @param key key string
     * @return value if key-value pair exists, null if not.
     */
    public String getFromStorage(String key){
        return membuffer.get(key);
    }

    /**
     * Insert key value pair into storage
     * @param key Key String
     * @param value Value String
     * @throws IOException Thrown if program had problem serialize the data
     */
    public void putToStorage(String key, String value) throws IOException {
        membuffer.put(key,value);
        store();
    }

    /**
     * Clear the tablet and its corresponding file
     */
    public void clear(){
        // remove this tablet from file system
        membuffer.clear();
        File file = new File(filepath+"/"+id.toString());
        file.delete();
        if(file.getParentFile().listFiles().length == 0){
            file.getParentFile().delete();
        }
    }


    /**
     * Create a new tablet
     * @param path tablet storage path
     * @return KVTablet instance
     */
    public static KVTablet createNewTablet(String path){
        KVTablet newtablet = new KVTablet();
        newtablet.filepath = path;
        newtablet.id = UUID.randomUUID();
        newtablet.membuffer = new ConcurrentHashMap<>();
        return newtablet;
    }

    /**
     * load a tablet from folder using UUID
     * @param folderpath string representation of the folder path
     * @param uuid uuid corresponding to this tablet.
     * @return KVtablet instance if the tablet exists and loaded correctly. Null if not.
     */
    public static KVTablet load(String folderpath, UUID uuid){
        KVTablet newtable = new KVTablet();
        Path fileLocation = Paths.get(folderpath+"/"+uuid.toString());
        try{
            byte[] data = Files.readAllBytes(fileLocation);
            ByteArrayInputStream inStream = new ByteArrayInputStream(data);
            ObjectInputStream stream = new ObjectInputStream(inStream);
            ConcurrentHashMap<String,String> buffer = (ConcurrentHashMap<String, String>) stream.readObject();
            stream.close();
            newtable.id = uuid;
            newtable.membuffer = buffer;
            newtable.filepath = folderpath;
            return newtable;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load a tablet using file object
     * @param tablet file object representation of the tablet.
     * @return KVTablet instance if loaded successfully, NUll if not.
     */
    public static  KVTablet load(File tablet){
        try {
            return KVTablet.load(tablet.getParent(),UUID.fromString(tablet.getName()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Store the current tablet onto disk
     * @throws IOException thrown if there were IOError occurred.
     */
    private void store() throws IOException {
        File targetFile = new File(filepath + "/" + id.toString());
        if(!targetFile.getParentFile().exists()){
            targetFile.getParentFile().mkdirs();
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(outStream);
        stream.writeObject(membuffer);
        stream.close();
        FileOutputStream out = new FileOutputStream(filepath+"/"+id.toString());
        out.write(outStream.toByteArray());
    }

    /**
     * Return set of keys that this tablet holds
     * @return Set of string
     */
    public Set<String> getKeys(){
        return membuffer.keySet();
    }

    /**
     * Get number of entry are in this tablet.
     * @return
     */
    public int getEntryCount(){
        return membuffer.size();
    }

    /**
     * Return the ID of this tablet.
     * @return UUID instance
     */
    public UUID getID(){
        return id;
    }
}
