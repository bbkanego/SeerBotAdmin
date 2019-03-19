package com.seerlogics.botadmin.exception;

/**
 * Created by bkane on 3/18/19.
 */
public class BaseRuntimeException extends RuntimeException {
    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public BaseRuntimeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseRuntimeException(String errorCode) {
        this.errorCode = errorCode;
    }
}
