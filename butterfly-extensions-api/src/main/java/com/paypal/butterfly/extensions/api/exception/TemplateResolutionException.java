package com.paypal.butterfly.extensions.api.exception;

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
     * @param exception reason why a transformation template could not be chosen
     */
    public TemplateResolutionException(String message, Exception exception) {
        super(message, exception);
    }

}
