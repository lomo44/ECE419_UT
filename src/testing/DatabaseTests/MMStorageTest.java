package testing.DatabaseTests;

import static org.junit.Assert.*;

import java.io.IOException;

import junit.framework.TestCase;
import org.junit.Test;

import database.storage.KVStorage;
import database.storage.MMStorage;

public class MMStorageTest extends TestCase{

	@Test
	public void test() throws Exception {
		KVStorage store = new MMStorage(2000);
		store.putToStorage("123", "123");
		store.putToStorage("456", "456");
		store.putToStorage("789", "789");
		assertEquals(store.getFromStorage("123"),"123");
		store.clearStorage();
	}

}
