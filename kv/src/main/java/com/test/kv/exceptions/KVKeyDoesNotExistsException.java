package com.test.kv.exceptions;

/**
 * This is thrown when a key user trying to look up is not present in storage
 */
public class KVKeyDoesNotExistsException extends KVException {
    /**
     * @param message provides more details about the cause and potential ways to debug/address.
     */
    public KVKeyDoesNotExistsException(String message) { super(message); }

    /**
     * @param message provides more details about the cause and potential ways to debug/address.
     * @param cause Cause of the exception
     */
    public KVKeyDoesNotExistsException(String message, Throwable cause) { super(message, cause); }
}
