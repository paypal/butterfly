package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.exception.ButterflyRuntimeException;

/**
 * This type represents an unexpected behavior
 * during the transformation process that was not
 * caused by the transformation template itself,
 * neither its input, but just a Butterfly
 * malfunction
 *
 * @author facarvalho
 */
class InternalException extends ButterflyRuntimeException {

    InternalException(String exceptionMessage) {
        super(exceptionMessage);
    }

    InternalException(String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }

}
