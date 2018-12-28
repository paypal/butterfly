package com.test;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

// Adding this class as well just so SampleTransformationTemplate is
// the second to be added, then we can test shortcut as number 2,
// instead of 1, which would be too trivial
public class DummyTransformationTemplate extends TransformationTemplate {

    @Override
    public Class<? extends Extension> getExtensionClass() {
        return null;
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