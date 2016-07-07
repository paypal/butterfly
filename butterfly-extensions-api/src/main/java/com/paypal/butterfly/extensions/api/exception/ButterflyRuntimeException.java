package com.paypal.butterfly.extensions.api.exception;

/**
 * Butterfly external facing generic runtime exception.
 * This exception type will be available during compilation time
 * for any other project that integrates with Butterfly.
 * It is meant to be extended by more specific type, that is why
 * it is abstract.
 *
 * @author facarvalho
 */
public abstract class ButterflyRuntimeException extends RuntimeException {

    public ButterflyRuntimeException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ButterflyRuntimeException(String exceptionMessage, Exception exception) {
        super(exceptionMessage, exception);
    }

}
