package com.test.kv.exceptions;

/**
 * This is thrown when user trying to look up a expired key
 */
public class KVKeyExpiredException extends Exception {
    /**
     * @param message provides more details about the cause and potential ways to debug/address.
     */
    public KVKeyExpiredException(String message) { super(message); }

    /**
     * @param message provides more details about the cause and potential ways to debug/address.
     * @param cause Cause of the exception
     */
    public KVKeyExpiredException(String message, Throwable cause) { super(message, cause); }
}
