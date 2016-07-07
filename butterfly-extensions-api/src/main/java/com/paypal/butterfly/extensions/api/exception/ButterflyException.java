package com.paypal.butterfly.extensions.api.exception;

/**
 * Butterfly external facing generic exception.
 * This exception type will be available during compilation time
 * for any other project that integrates with Butterfly.
 * It is meant to be extended by more specific type, that is why
 * it is abstract.
 *
 * @author facarvalho
 */
public abstract class ButterflyException extends Exception {

    public ButterflyException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ButterflyException(String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }

}
