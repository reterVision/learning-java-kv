package com.test.kv.lib.storage;

import junit.framework.TestCase;

import com.test.kv.exceptions.KVKeyDoesNotExistsException;

/**
 * MemoryTest contains Memory Class Unit Test
 */
public class MemoryTest extends TestCase {

    public void testGet() throws Exception {
        Memory<String, Object> testMemory = new Memory();
        try {
            testMemory.Get("NoSuchKey");
        } catch (KVKeyDoesNotExistsException e) {
            assertNotNull(e);
        }

        testMemory.Set("testKey", "testValue");
        String value = (String)testMemory.Get("testKey");
        assertEquals("testValue", value);
    }

    public void testSet() throws Exception {
        Memory<String, String> testMemory = new Memory();
        Boolean success = testMemory.Set("testKey", "testValue");
        assertTrue(success);
    }

    public void testSetEx() throws Exception {
        Memory<String, String> testMemory = new Memory();
        Boolean success = testMemory.SetEx("testKey", "testValue", new Long(1));
        assertTrue(success);
        Thread.currentThread().sleep(1000); // TODO find a better way to do time related unit testing
        String value = testMemory.Get("testKey");
        assertNull(value);
    }

    public void testDel() throws Exception {
        Memory<String, String> testMemory = new Memory(100);
        Boolean successSet = testMemory.Set("testKey", "testValue");
        assertTrue(successSet);
        Boolean successDel = testMemory.Del("testKey");
        assertTrue(successDel);

        try {
            testMemory.Del("NoSuchKey");
        } catch (KVKeyDoesNotExistsException e) {
            assertNotNull(e);
        }
    }

    public void testLRU() throws Exception {
        Memory<String, String> testMemory = new Memory(1);
        Boolean successSet1 = testMemory.Set("testKey1", "testValue1");
        assertTrue(successSet1);
        String value1 = testMemory.Get("testKey1");
        assertEquals("testValue1", value1);

        Boolean successSet2 = testMemory.Set("testKey2", "testValue2");
        assertTrue(successSet2);
        value1 = testMemory.Get("testKey1");
        assertNull(value1);
        String value2 = testMemory.Get("testKey2");
        assertEquals("testValue2", value2);
    }
}