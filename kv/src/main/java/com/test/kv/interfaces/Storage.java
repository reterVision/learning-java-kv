package com.test.kv.interfaces;

import com.test.kv.exceptions.KVKeyDoesNotExistsException;
import com.test.kv.exceptions.KVKeyExpiredException;

/**
 * Interface for storage
 */
public interface Storage<KeyType, ValueType> {
    /**
     * Get method returns the value of user specified key in storage if it exists
     *
     * @throws KVKeyDoesNotExistsException when key is not presented in storage
     */
    ValueType Get(KeyType key) throws KVKeyDoesNotExistsException, KVKeyExpiredException;

    /**
     * Set method adds or updates a key -> value pair in storage with ttl config
     */
    Boolean Set(KeyType key, ValueType value);

    /**
     * SetEx method adds or updates a key -> value pair in storage with ttl config
     */
    Boolean SetEx(KeyType key, ValueType value, Long ttlInSec);

    /**
     * Del method deletes a key in storage
     *
     * @throws KVKeyDoesNotExistsException when key is not presented in storage
     */
    Boolean Del(KeyType key) throws KVKeyDoesNotExistsException;
}

