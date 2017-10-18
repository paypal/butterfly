package com.extensiontest;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;

import java.io.File;

/**
 * Sample extension to migrate the sample-app
 * from a WAR deployment application model to a Spring Boot
 * application model
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
        return "1.0.0";
    }

    @Override
    public Class<? extends TransformationTemplate> automaticResolution(File file) throws TemplateResolutionException {
        return SampleTransformationTemplate.class;
    }

}
