package com.test.kv.lib.storage;

import com.test.kv.exceptions.KVKeyExpiredException;
import junit.framework.TestCase;

import com.test.kv.exceptions.KVKeyDoesNotExistsException;

/**
 * MemoryStorageTest contains MemoryStorage Class Unit Test
 */
public class MemoryStorageTest extends TestCase {

    public void testGet() throws Exception {
        MemoryStorage<String, Object> testMemoryStorage = new MemoryStorage();
        try {
            testMemoryStorage.Get("NoSuchKey");
        } catch (KVKeyDoesNotExistsException e) {
            assertNotNull(e);
        }

        testMemoryStorage.Set("testKey", "testValue");
        String value = (String) testMemoryStorage.Get("testKey");
        assertEquals("testValue", value);
    }

    public void testSet() throws Exception {
        MemoryStorage<String, String> testMemoryStorage = new MemoryStorage();
        Boolean success = testMemoryStorage.Set("testKey", "testValue");
        assertTrue(success);
    }

    public void testSetEx() throws Exception {
        MemoryStorage<String, String> testMemoryStorage = new MemoryStorage();
        Boolean success = testMemoryStorage.SetEx("testKey", "testValue", new Long(1));
        assertTrue(success);
        Thread.currentThread().sleep(1000); // TODO find a better way to do time related unit testing

        try {
            testMemoryStorage.Get("testKey");
        } catch (KVKeyExpiredException e) {
            assertNotNull(e);
        }
    }

    public void testDel() throws Exception {
        MemoryStorage<String, String> testMemoryStorage = new MemoryStorage(100);
        Boolean successSet = testMemoryStorage.Set("testKey", "testValue");
        assertTrue(successSet);
        Boolean successDel = testMemoryStorage.Del("testKey");
        assertTrue(successDel);

        try {
            testMemoryStorage.Del("NoSuchKey");
        } catch (KVKeyDoesNotExistsException e) {
            assertNotNull(e);
        }
    }

    public void testLRU() throws Exception {
        MemoryStorage<String, String> testMemoryStorage = new MemoryStorage(1);
        Boolean successSet1 = testMemoryStorage.Set("testKey1", "testValue1");
        assertTrue(successSet1);
        String value1 = testMemoryStorage.Get("testKey1");
        assertEquals("testValue1", value1);

        Boolean successSet2 = testMemoryStorage.Set("testKey2", "testValue2");
        assertTrue(successSet2);
        value1 = testMemoryStorage.Get("testKey1");
        assertNull(value1);
        String value2 = testMemoryStorage.Get("testKey2");
        assertEquals("testValue2", value2);
    }
}