package com.paypal.butterfly.extensions.api.exception;

/**
 * Butterfly external facing generic exception.
 * This exception type will be available during compilation time
 * for any other project that integrates with Butterfly.
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
