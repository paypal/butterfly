package com.paypal.butterfly.facade.exception;

import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;

import java.util.Set;

/**
 * This exception is thrown by {@link com.paypal.butterfly.facade.ButterflyFacade#automaticResolution(File)}
 * whenever more than one transformation template is identified as applicable to the application
 *
 * @see {@link com.paypal.butterfly.facade.ButterflyFacade#automaticResolution(File)}
 *
 * @author facarvalho
 */
public class TemplateResolutionException extends ButterflyException {

    public TemplateResolutionException(Set<Class<? extends TransformationTemplate>> resolvedTemplates) {
        super("More than one transformation template was resolved, they are: " + resolvedTemplates);
    }

}
