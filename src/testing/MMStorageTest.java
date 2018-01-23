package testing;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import database.storage.KVStorage;
import database.storage.MMStorage;

public class MMStorageTest {

	@Test
	public void test() {
	 try {
		KVStorage store = new MMStorage(2000);
		store.putToStorage("123", "123");
		store.putToStorage("456", "456");
		store.putToStorage("789", "789");
		store.getFromStorage("123");
		//((MMStorage) store).test();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
