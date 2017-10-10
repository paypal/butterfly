package com.paypal.butterfly.extensions.api.exception;

/**
 * Butterfly generic checked exception.
 *
 * @author facarvalho
 */
public class ButterflyException extends Exception {

    public ButterflyException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ButterflyException(String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }

}
