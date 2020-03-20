package com.paypal.butterfly.extensions.api.exception;

/**
 * Thrown whenever the application to be transformed is not in a valid state.
 *
 * @author facarvalho
 */
public class ApplicationValidationException extends ButterflyRuntimeException {

    public ApplicationValidationException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ApplicationValidationException(String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }
}
