package com.paypal.butterfly.extensions.api.exception;

/**
 * Butterfly application validation exception.
 * This exception should be used whenever the application to be
 * transformed is not in a good initial state, which invalidates
 * a transformation attempt
 *
 * @author facarvalho
 */
public class ApplicationValidationException extends ButterflyRuntimeException {

    public ApplicationValidationException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ApplicationValidationException(String exceptionMessage, Exception exception) {
        super(exceptionMessage, exception);
    }
}
