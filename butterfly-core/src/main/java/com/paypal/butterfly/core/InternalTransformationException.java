package com.paypal.butterfly.core;

import com.paypal.butterfly.api.exception.TransformationException;

/**
 * Internal representation of transformation exceptions.
 * It carries with it the latest transformation context associated with
 * the transformation that was aborted.
 *
 * @author facarvalho
 */
class InternalTransformationException extends TransformationException {

    private TransformationContextImpl transformationContext;

    InternalTransformationException(String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }

    InternalTransformationException(String exceptionMessage, TransformationContextImpl transformationContext) {
        super(exceptionMessage);
        this.transformationContext = transformationContext;
    }

    InternalTransformationException(String exceptionMessage, Throwable throwable, TransformationContextImpl transformationContext) {
        super(exceptionMessage, throwable);
        this.transformationContext = transformationContext;
    }

    InternalTransformationException(TransformationException transformationException, TransformationContextImpl transformationContext) {
        this(transformationException.getMessage(), transformationException, transformationContext);
    }

    // This method's visibility is intentionally being set to package
    @SuppressWarnings("PMD.DefaultPackage")
    TransformationContextImpl getTransformationContext() {
        return transformationContext;
    }

}
