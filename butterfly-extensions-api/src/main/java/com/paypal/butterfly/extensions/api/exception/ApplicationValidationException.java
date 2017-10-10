package com.paypal.butterfly.extensions.api.exception;

/**
 * Thrown whenever the application to be transformed is not in a valid state.
 *
 * @author facarvalho
 */
// TODO this class is not actually being used anywhere at this moment
public class ApplicationValidationException extends ButterflyRuntimeException {

    public ApplicationValidationException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ApplicationValidationException(String exceptionMessage, Exception exception) {
        super(exceptionMessage, exception);
    }
}
