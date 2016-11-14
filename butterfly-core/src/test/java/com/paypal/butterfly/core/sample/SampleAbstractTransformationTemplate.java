package com.paypal.butterfly.core.sample;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * Created by vkuncham on 11/7/2016.
 */
public abstract class SampleAbstractTransformationTemplate extends TransformationTemplate {
    @Override
    public Class<? extends Extension> getExtensionClass() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
