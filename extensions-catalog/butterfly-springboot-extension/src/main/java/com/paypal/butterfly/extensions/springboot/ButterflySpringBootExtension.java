package com.paypal.butterfly.extensions.springboot;

import java.io.File;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * Butterfly Spring Boot extension
 *
 * @author facarvalho
 */
public class ButterflySpringBootExtension extends Extension {

    public ButterflySpringBootExtension() {
        add(JavaEEToSpringBoot.class);
        add(SpringBootUpgrade_1_5_6_to_1_5_7.class);
    }

    @Override
    public String getDescription() {
        return "Butterfly Spring Boot extension";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public Class<? extends TransformationTemplate> automaticResolution(File file) {
        // TODO
        return null;
    }

}
