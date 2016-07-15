package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.facade.Configuration;

import java.io.File;

/**
 * Represents an specific transformation to be applied
 * against a specific application
 *
 * @author facarvalho
 */
public class Transformation {

    // TODO Replace this by an actual JSON object mapper
    private static final String TO_STRING_SYNTAX = "{ \"application\" : %s, \"template\" : %s, \"templateClass\" : %s }";

    private Application application;
    private TransformationTemplate template;
    private Configuration configuration;
    private File transformedApplicationLocation;

    public Transformation(Application application, TransformationTemplate template, Configuration configuration) {
        this.application = application;
        this.template = template;
        this.configuration = configuration;
    }

    public void setTransformedApplicationLocation(File transformedApplicationLocation) {
        this.transformedApplicationLocation = transformedApplicationLocation;
    }

    public Application getApplication() {
        return application;
    }

    public TransformationTemplate getTemplate() {
        return template;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public File getTransformedApplicationLocation() {
        return transformedApplicationLocation;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_SYNTAX, application, template, template.getClass().getName());
    }

}
