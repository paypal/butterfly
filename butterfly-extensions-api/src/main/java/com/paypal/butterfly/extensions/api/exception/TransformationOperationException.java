package com.paypal.butterfly.extensions.api.exception;

import com.paypal.butterfly.extensions.api.TransformationOperation;

/**
 * Thrown whenever an unexpected behavior or result
 * during execution of a {@link com.paypal.butterfly.extensions.api.TransformationOperation}.
 * This might cause a transformation abortion or not,
 * depending on {@link TransformationOperation#abortOnFailure(boolean)}.
 *
 * @author facarvalho
 */
public class TransformationOperationException extends TransformationUtilityException {

    public TransformationOperationException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public TransformationOperationException(String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }

}
