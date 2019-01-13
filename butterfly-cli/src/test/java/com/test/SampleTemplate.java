package com.test;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * @author facarvalho
 */
public class SampleTemplate extends TransformationTemplate {

    @Override
    public Class<? extends Extension> getExtensionClass() {
        return SampleExtension.class;
    }

    @Override
    public String getDescription() {
        return "SampleTemplate for tests purposes";
    }

    @Override
    public String getApplicationType() {
        return null;
    }

    @Override
    public String getApplicationName() {
        return null;
    }

}
