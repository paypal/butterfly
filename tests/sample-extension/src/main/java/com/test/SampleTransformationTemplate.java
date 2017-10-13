package com.test;


import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * Sample transformation template
 *
 * @author facarvalho
 */
public class SampleTransformationTemplate extends TransformationTemplate {

    public SampleTransformationTemplate() {
        // TODO
    }

    @Override
    public Class<? extends Extension> getExtensionClass() {
        return SampleExtension.class;
    }

    @Override
    public String getDescription() {
        return "Sample transformation template";
    }

}
