package com.test;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

import java.io.File;
import java.util.Optional;

/**
 * @author facarvalho
 */
public class SampleExtension2 extends Extension {

    public SampleExtension2() {
        add(DummyTemplate.class);
    }

    @Override
    public String getDescription() {
        return "SampleExtension2 for tests purposes";
    }

    @Override
    public String getVersion() {
        return "1.5.0";
    }

    @Override
    public Optional<Class<? extends TransformationTemplate>> automaticResolution(File applicationFolder) {
        return Optional.empty();
    }

}
