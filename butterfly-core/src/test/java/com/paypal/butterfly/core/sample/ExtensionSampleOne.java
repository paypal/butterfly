package com.paypal.butterfly.core.sample;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

import java.io.File;

public class ExtensionSampleOne extends Extension {

    @Override
    public String getDescription() {
        return "Sample extension class";
    }

    @Override
    public Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) {

        if(applicationFolder.getAbsolutePath().contains("testTransformation1")) {
            return null;
        } else {
            return SampleTransformationTemplate.class;
        }
    }

}