package com.test.kv.exceptions;

/**
 * Abstract class for exceptions of the KV.
 */
public class KVException extends Exception {
    /**
     * Constructor.
     *
     * @param message Message of with details of the exception.
     */
    public KVException(String message) { super(message); }

    /**
     * Constructor.
     *
     * @param message Message with details of the exception.
     * @param cause Cause.
     */
    public KVException(String message, Throwable cause) { super(message, cause); }
}
