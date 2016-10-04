package com.test;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

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

}

// Adding this class as well just so SampleTransformationTemplate is
// the second to be added, then we can test shortcut as number 2,
// instead of 1, which would be too trivial
class DummyTransformationTemplate extends TransformationTemplate {

    @Override
    public Class<? extends Extension> getExtensionClass() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}