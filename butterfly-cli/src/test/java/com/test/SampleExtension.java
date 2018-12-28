package com.test;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

import java.io.File;

/**
 * @author facarvalho
 */
public class SampleExtension extends Extension {

    public SampleExtension() {
        add(DummyTransformationTemplate.class);
        add(SampleTransformationTemplate.class);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) {
        return null;
    }

}
