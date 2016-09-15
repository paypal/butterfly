package com.paypal.butterfly.extensions.api.exception;

/**
 * This type represents an unexpected behavior or result
 * during execution of a transformation utility.
 * Transformation utility exceptions ALWAYS abort the
 * transformation process.
 *
 * @author facarvalho
 */
public class TransformationUtilityException extends ButterflyRuntimeException {

    public TransformationUtilityException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public TransformationUtilityException(String exceptionMessage, Exception exception) {
        super(exceptionMessage, exception);
    }

}
