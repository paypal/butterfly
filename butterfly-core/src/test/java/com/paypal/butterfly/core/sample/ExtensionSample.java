package com.paypal.butterfly.core.sample;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;

import java.io.File;

public class ExtensionSample extends Extension {

    @Override
    public String getDescription() {
        return "Sample extension class";
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) throws TemplateResolutionException {
        if(applicationFolder.getAbsolutePath().contains("testTransformation1")) {
            throw new TemplateResolutionException("No transformation template applies");
        } else {
            return SampleTransformationTemplate.class;
        }
    }

}