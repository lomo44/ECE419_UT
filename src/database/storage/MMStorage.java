package database.storage;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class MMStorage implements KVStorage{

	private static String storageName = "tmp/database.txt";
	private ConcurrentHashMap<String,String> storageOnMem;
	private FileChannel fileChannel;
	private MappedByteBuffer buffer;
	private File storage;
	private long sizeofStorage;
	
	public MMStorage(long size) throws IOException, ClassNotFoundException {
		sizeofStorage = size;
		storage = new File(storageName);
		storage.getParentFile().mkdir();
		storage.createNewFile();
	    fileChannel = new RandomAccessFile(storage,"rw").getChannel();
	    buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, sizeofStorage);
	    storageOnMem = injectFileToMem(buffer,storageOnMem);
        if(storageOnMem==null){
          storageOnMem = new ConcurrentHashMap<>();
        }
	}
	
	public byte[] serialize(Map<String, String> map) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ObjectOutputStream stream = new ObjectOutputStream(outStream);
		stream.writeObject(map);
		stream.close();
		return outStream.toByteArray();
	}
	
	public Map<String, String> deserialize(byte[] input) throws IOException, ClassNotFoundException {
		ByteArrayInputStream inStream = new ByteArrayInputStream(input);
		ObjectInputStream stream = new ObjectInputStream(inStream);
		ConcurrentHashMap<String, String>map = (ConcurrentHashMap<String, String>) stream.readObject();
		stream.close();
		return map;
	}
	
	private ConcurrentHashMap<String,String> injectFileToMem( MappedByteBuffer buf , Map<String, String> map) throws ClassNotFoundException, IOException {
		int limit = buf.getInt();
		if (limit!=0) {
		buf.limit(limit);
		byte[] input1= new byte[buf.remaining()];
		buf.get(input1);
		return (ConcurrentHashMap<String, String>) deserialize(input1);
		}
		return null;
	}
	
	@Override
	public boolean inStorage(String key) {
		return this.storageOnMem.containsKey(key);

	}

	@Override
	public synchronized String getFromStorage(String key) throws Exception {
        String value=storageOnMem.get(key);
        if (value==null) throw new NoSuchElementException("Key not found in cache.");
        return value;
	}

	@Override
	public synchronized void putToStorage(String key, String value) throws Exception {
		storageOnMem.put(key, value);
		buffer.clear();
		byte[] output = serialize(storageOnMem);
		buffer.putInt(output.length+4);
		buffer.put(output);
	}

	@Override
	public synchronized void clearStorage() throws IOException {
		buffer.clear();
		storageOnMem.clear();
		byte[] output = serialize(storageOnMem);
		buffer.putInt(output.length+4);
		buffer.put(output);
	}

}

