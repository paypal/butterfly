package com.test;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * @author facarvalho
 */
public class SampleTransformationTemplate extends TransformationTemplate {

    @Override
    public Class<? extends Extension> getExtensionClass() {
        return SampleExtension.class;
    }

    @Override
    public String getDescription() {
        return null;
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
