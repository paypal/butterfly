package com.paypal.butterfly.extensions.api.exception;

/**
 * Butterfly generic runtime exception.
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
