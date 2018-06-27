package com.paypal.butterfly.test;

/**
 * Runtime exception thrown whenever a Butterfly assertion fails
 *
 * @author facarvalho
 */
public class FailedAssertException extends RuntimeException {

    public FailedAssertException() {
    }

    public FailedAssertException(String message) {
        super(message);
    }

    public FailedAssertException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedAssertException(Throwable cause) {
        super(cause);
    }

}
