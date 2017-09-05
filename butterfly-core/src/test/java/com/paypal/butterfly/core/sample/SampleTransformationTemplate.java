package com.paypal.butterfly.core.sample;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * Created by vkuncham on 11/7/2016.
 */
public class SampleTransformationTemplate extends TransformationTemplate {

    @Override
    public Class<? extends Extension> getExtensionClass() {
        return ExtensionSample.class;
    }

    @Override
    public String getDescription() {
        return "Butterfly extension";
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
