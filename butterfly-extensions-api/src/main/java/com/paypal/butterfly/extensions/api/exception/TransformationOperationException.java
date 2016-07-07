package com.paypal.butterfly.extensions.api.exception;

/**
 * This type represents an unexpected behavior or result
 * during execution of a transformation operation.
 * This might cause a transformation abortion or not,
 * depending on operation configuration
 *
 * @author facarvalho
 */
public class TransformationOperationException extends ButterflyException {

    public TransformationOperationException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public TransformationOperationException(String exceptionMessage, Exception exception) {
        super(exceptionMessage, exception);
    }

}
