package com.paypal.butterfly.extensions.api.exception;

import java.io.File;

/**
 * Thrown by {@link com.paypal.butterfly.extensions.api.Extension#automaticResolution(File)}
 * whenever a transformation template cannot be resolved.
 *
 * @author facarvalho
 */
public class TemplateResolutionException extends ButterflyException {

    /**
     * This exception is thrown by {@link com.paypal.butterfly.extensions.api.Extension#automaticResolution(File)}
     * whenever transformation template cannot be resolved
     *
     * @param message explains why a transformation template could not be chosen
     */
    public TemplateResolutionException(String message) {
        super(message);
    }

    /**
     * This exception is thrown by {@link com.paypal.butterfly.extensions.api.Extension#automaticResolution(File)}
     * whenever transformation template cannot be resolved
     *
     * @param message explains why a transformation template could not be chosen
     * @param throwable reason why a transformation template could not be chosen
     */
    public TemplateResolutionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
