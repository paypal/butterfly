package com.paypal.butterfly.extensions.api.exception;

/**
 * Exception to be used when a transformation template definition
 * is not well formed
 */
public class TransformationDefinitionException extends ButterflyRuntimeException {

    public TransformationDefinitionException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public TransformationDefinitionException(String exceptionMessage, Exception exception) {
        super(exceptionMessage, exception);
    }

}
