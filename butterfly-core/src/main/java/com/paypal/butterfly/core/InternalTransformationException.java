package com.paypal.butterfly.core;

import com.paypal.butterfly.facade.exception.TransformationException;

/**
 * Internal representation of transformation exceptions.
 * It carries with it the latest transformation context associated with
 * the transformation that was aborted.
 *
 * @author facarvalho
 */
class InternalTransformationException extends TransformationException {

    TransformationContextImpl transformationContext;

    InternalTransformationException(String exceptionMessage, TransformationContextImpl transformationContext) {
        super(exceptionMessage);
        this.transformationContext = transformationContext;
    }

    InternalTransformationException(String exceptionMessage, Exception exception, TransformationContextImpl transformationContext) {
        super(exceptionMessage, exception);
        this.transformationContext = transformationContext;
    }

    InternalTransformationException(TransformationException e, TransformationContextImpl transformationContext) {
        this(e.getMessage(), e, transformationContext);
    }

    TransformationContextImpl getTransformationContext() {
        return transformationContext;
    }

}
