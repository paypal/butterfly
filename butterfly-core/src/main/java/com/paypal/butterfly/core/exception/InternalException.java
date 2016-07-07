package com.paypal.butterfly.core.exception;

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
public class InternalException extends ButterflyRuntimeException {

    public InternalException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public InternalException(String exceptionMessage, Exception exception) {
        super(exceptionMessage, exception);
    }

}
