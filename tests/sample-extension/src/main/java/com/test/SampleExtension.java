package com.test;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;

import java.io.File;

/**
 * Sample extension
 *
 * @author facarvalho
 */
public class SampleExtension extends Extension {

    public SampleExtension() {
        add(SampleTransformationTemplate.class);
    }

    @Override
    public String getDescription() {
        return "Sample extension";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public Class<? extends TransformationTemplate> automaticResolution(File file) throws TemplateResolutionException {
        throw new TemplateResolutionException("No transformation template could be resolved");
    }

}
