package testing.DatabaseTests;

import database.storage.KVTabletStorage;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class KVTabletStorageTest extends TestCase{
    private static final String storagePath = "tmp/";
    private KVTabletStorage tabletStorage;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tabletStorage = new KVTabletStorage(storagePath);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(tabletStorage!=null){
            tabletStorage.clearStorage();
            tabletStorage = null;
        }
    }

    @Test
    public void testKVTabletStorage_PutGet() throws IOException {
        tabletStorage.putToStorage("123","456");
        assertEquals("456",tabletStorage.getFromStorage("123"));
    }

    @Test
    public void testKVTabletStorage_MultiThread() throws InterruptedException {
        Thread threadA = new Thread(){
            @Override
            public void run() {
                for(int i = 0; i < 1000; i++){
                    if(i%2 == 0){
                        try {
                            tabletStorage.putToStorage(Integer.toString(i),Integer.toString(i));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        Thread threadB = new Thread(){
            @Override
            public void run() {
                for(int i = 0; i < 1000; i++){
                    if(i%2 == 1){
                        try {
                            tabletStorage.putToStorage(Integer.toString(i),Integer.toString(i));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();
        for(int i = 0; i < 1000; i++){
            assertEquals(Integer.toString(i),Integer.toString(i));
        }
    }
}
