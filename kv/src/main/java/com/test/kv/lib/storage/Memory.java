package com.test.kv.lib.storage;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.test.kv.exceptions.KVKeyDoesNotExistsException;
import com.test.kv.exceptions.KVKeyExpiredException;
import com.test.kv.interfaces.Storage;

/**
 * Implementation of Storage -- In memory store with LRU
 */
public class Memory<KeyType, ValueType> implements Storage<KeyType, ValueType> {
    /**
     * storage is the actual store where we keep all key->value pairs
     */
    protected Map<KeyType, ValueType> storage;

    /**
     * maxKeySize is the max number of keys storage can store
     */
    protected int maxKeySize = 1024;

    /**
     * expireAtStorage stores the key->TTL pair for cleanup expired keys usage
     */
    protected Map<KeyType, Long> expireAtStorage;

    /**
     * expiringQueue maintains the keys in a queue for LRU
     */
    protected ConcurrentLinkedQueue<KeyType> expiringQueue;

    /**
     * expiringQueueLock is a mutex that protects expiringQueue
     */
    protected static final Object expiringQueueLock = new Object();

    /**
     * static logger
     */
    private static final Logger log = Logger.getLogger(Memory.class.getName());

    /**
     * Constructor
     */
    public Memory() {
        this(0); // If user doesn't specify cache size, use the default value
    }

    /**
     * Constructor
     *
     * @param maxKeySize the maximum size of keys Memory can store
     */
    public Memory(int maxKeySize) {
        if (this.maxKeySize != 0) {
            this.maxKeySize = maxKeySize;
        }
        this.storage = new ConcurrentHashMap<KeyType, ValueType>(this.maxKeySize);
        this.expireAtStorage = new ConcurrentHashMap<KeyType, Long>(this.maxKeySize);
        this.expiringQueue = new ConcurrentLinkedQueue<KeyType>();
    }

    /**
     * updateKeyRecentUsage updates the key in expiringQueue
     *
     * @param key the the to be updated in expiringQueue
     */
    private void updateKeyRecentUsage(KeyType key) {
        synchronized (expiringQueueLock) {
            expiringQueue.remove(key);
            expiringQueue.offer(key);
        }
    }

    /**
     * removeLeastRecentUsedKey removes the key which is least recently used
     */
    private void removeLeastRecentUsedKey() {
        synchronized (expiringQueueLock) {
            if (storage.size() < maxKeySize) { // ensure we still need to delete keys
                return;
            }
            KeyType key = expiringQueue.poll();
            if (key != null) {
                storage.remove(key);
            }
        }
    }

    /**
     * storeKeyAndTTL stores the key with its ttl in expireAtStorage
     */
    private void storeKeyAndTTL(KeyType key, Long ttlInSec) {
        Long expiredAt = new Long(Instant.now().getEpochSecond() + ttlInSec.longValue());
        expireAtStorage.put(key, expiredAt);
    }

    /**
     * keyExpired removes the keys that are expired
     *
     * @return if the key is expired and been cleared
     */
    private boolean keyExpired(KeyType key) {
        Long now = new Long(Instant.now().getEpochSecond());
        Long expiredAt = expireAtStorage.get(key);
        if (expiredAt != null && now >= expiredAt) {
            expireAtStorage.remove(key);
            storage.remove(key);
            return true;
        }
        return false;
    }

    /**
     * Get implements Storage interface
     *
     * @param key the key to be looked up in storage
     * @return the value corresponding to key
     * @throws KVKeyDoesNotExistsException
     */
    public ValueType Get(KeyType key) throws KVKeyDoesNotExistsException, KVKeyExpiredException {
        if (keyExpired(key)) {
            throw new KVKeyExpiredException("key " + key + " expired");
        }
        try {
            ValueType value = storage.get(key);
            if (value == null) {
                throw new KVKeyDoesNotExistsException("key " + key + " not exists");
            }
            updateKeyRecentUsage(key); // update key in queue as it just get fetched
            return value;
        } catch (Exception e) {
            log.log(Level.WARNING, "Exception encountered in Get", e);
        }
        return null;
    }

    /**
     * Set implements Storage interface
     *
     * @param key the key to be set in storage
     * @param value the value to be set with key in storage
     * @return if Set operation succeed or not
     */
    public Boolean Set(KeyType key, ValueType value) {
        try {
            if (!storage.containsKey(key)) {
                if (storage.size() >= maxKeySize) {
                    removeLeastRecentUsedKey(); // key space is too large now, we need to clean up before insert more keys
                }
            }
            storage.put(key, value);
            updateKeyRecentUsage(key); // update key in queue as it just get fetched
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, "Exception encountered in Set", e);
        }
        return false;
    }

    /**
     * SetEx implements Storage interface
     *
     * @param key the key to be set in storage
     * @param value the value to be set with key in storage
     * @param ttlInSec time-to-live config of the key
     * @return if Set operation succeed or not
     */
    public Boolean SetEx(KeyType key, ValueType value, Long ttlInSec) {
        storeKeyAndTTL(key, ttlInSec);
        return Set(key, value);
    }

    /**
     * Del implements Storage interface
     *
     * @param key the key to be deleted in storage
     * @return if the key has been successfully deleted or not
     * @throws KVKeyDoesNotExistsException
     */
    public Boolean Del(KeyType key) throws KVKeyDoesNotExistsException {
        try{
            ValueType value = storage.remove(key);
            if (value == null) {
                throw new KVKeyDoesNotExistsException("Key " + key + " not exists");
            }
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, "Exception encountered in Del", e);
        }
        return false;
    }
}
