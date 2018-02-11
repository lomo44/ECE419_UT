package database.storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KVTablet extends Object{
    private ConcurrentHashMap<String,String> membuffer = null;
    private UUID id;
    private String filepath;

    private KVTablet(){
    }

    public boolean inStorage(String key) {
        return membuffer.contains(key);
    }

    public String getFromStorage(String key){
        return membuffer.get(key);
    }

    public void putToStorage(String key, String value){
        membuffer.put(key,value);
    }

    public void remove(){
        // remove this tablet from file system
        membuffer.clear();
        File file = new File(filepath+"/"+id.toString());
        file.delete();
    }

    public void close() throws IOException {
        store();
    }

    public static KVTablet createNewTablet(String path){
        KVTablet newtablet = new KVTablet();
        newtablet.filepath = path;
        newtablet.id = UUID.randomUUID();
        newtablet.membuffer = new ConcurrentHashMap<String,String>();
        return newtablet;
    }

    public static KVTablet load(String folderpath, UUID uuid) throws IOException, ClassNotFoundException {
        KVTablet newtable = new KVTablet();
        Path fileLocation = Paths.get(folderpath+"/"+uuid.toString());
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

    public void store() throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(outStream);
        stream.writeObject(membuffer);
        stream.close();
        FileOutputStream out = new FileOutputStream(filepath+"/"+id.toString());
        out.write(outStream.toByteArray());
    }

    public UUID getID(){
        return id;
    }
}
