package com.paypal.butterfly.extensions.api.exception;

/**
 * Butterfly external facing generic runtime exception.
 * This exception type will be available during compilation time
 * for any other project that integrates with Butterfly.
 *
 * @author facarvalho
 */
public class ButterflyRuntimeException extends RuntimeException {

    public ButterflyRuntimeException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ButterflyRuntimeException(String exceptionMessage, Exception exception) {
        super(exceptionMessage, exception);
    }

}
