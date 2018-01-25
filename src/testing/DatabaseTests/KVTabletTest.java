package testing.DatabaseTests;

import database.cache.KVLFUCache;
import database.storage.KVTablet;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class KVTabletTest extends TestCase{
    KVTablet tablet = null;
    private static final String tablets_path = "tmp/";
    @Override
    public void setUp() throws Exception{
        tablet = KVTablet.createNewTablet(tablets_path);
    }

    @Override
    public void tearDown() throws  Exception{
        if((tablet!=null)){
            tablet.remove();
        }
    }

    @Test
    public void testInsertAndGet(){
        tablet.putToStorage("a","b");
        assertEquals("b",tablet.getFromStorage("a"));
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        tablet.putToStorage("a","b");
        tablet.store();
        UUID id = tablet.getID();
        tablet = null;
        tablet = KVTablet.load(tablets_path,id);
        assertEquals("b",tablet.getFromStorage("a"));
    }
}
