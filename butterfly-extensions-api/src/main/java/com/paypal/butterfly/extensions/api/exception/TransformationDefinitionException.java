package com.paypal.butterfly.extensions.api.exception;

/**
 * Thrown whenever a transformation template definition is not well formed.
 *
 * @author facarvalho
 */
public class TransformationDefinitionException extends ButterflyRuntimeException {

    public TransformationDefinitionException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public TransformationDefinitionException(String exceptionMessage, Exception exception) {
        super(exceptionMessage, exception);
    }

}
