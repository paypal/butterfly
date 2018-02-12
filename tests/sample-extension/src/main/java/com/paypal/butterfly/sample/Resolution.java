package com.paypal.butterfly.sample;

import com.paypal.butterfly.extensions.api.TemplateResolution;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;

import java.io.File;

/**
 *
 * @author facarvalho
 */
public class Resolution implements TemplateResolution {

    public Class<? extends TransformationTemplate> automaticResolution(File file) throws TemplateResolutionException {
        return JavaEEToSpringBoot.class;
    }

}
