package com.test.kv.lib.storage;

import com.test.kv.exceptions.KVKeyExpiredException;
import junit.framework.TestCase;

import com.test.kv.exceptions.KVKeyDoesNotExistsException;

/**
 * MemoryStorageTest contains MemoryStorage Class Unit Test
 */
public class MemoryStorageTest extends TestCase {

    private long getTestTTLScalar() {
        return 1000000; // 1 millisecond = 1e+6 nanoseconds
    }


    public void testGet() throws Exception {
        MemoryStorage<String, Object> testMemoryStorage = new MemoryStorage();
        try {
            testMemoryStorage.Get("NoSuchKey");
            assertTrue(false); // intentionally fail the test if executed here.
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
        int mapSize = 1;
        long ttlScalar = getTestTTLScalar();

        MemoryStorage<String, String> testMemoryStorage = new MemoryStorage(mapSize, ttlScalar);
        Boolean success = testMemoryStorage.SetEx("testKey", "testValue", new Long(1));
        assertTrue(success);

        try {
            testMemoryStorage.Get("testKey");
            assertTrue(false); // intentionally fail the test if executed here.
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
            assertTrue(false); // intentionally fail the test if executed here.
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

        try {
            value1 = testMemoryStorage.Get("testKey1");
            assertTrue(false); // intentionally fail the test if executed here.
        } catch (KVKeyDoesNotExistsException e) {
            assertNotNull(e);
        }
        String value2 = testMemoryStorage.Get("testKey2");
        assertEquals("testValue2", value2);
    }
}