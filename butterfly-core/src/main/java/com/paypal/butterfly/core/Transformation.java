package com.paypal.butterfly.core;

import com.paypal.butterfly.facade.Configuration;

import java.io.File;

/**
 * Represents an specific transformation to be applied
 * against a specific application
 *
 * @author facarvalho
 */
public abstract class Transformation {

    // Application to be transformed
    private Application application;

    // Butterfly configuration object specific to this transformation
    private Configuration configuration;

    // The location where to place the transformed application
    private File transformedApplicationLocation;

    public Transformation(Application application, Configuration configuration) {
        this.application = application;
        this.configuration = configuration;
    }

    public void setTransformedApplicationLocation(File transformedApplicationLocation) {
        this.transformedApplicationLocation = transformedApplicationLocation;
    }

    public Application getApplication() {
        return application;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public File getTransformedApplicationLocation() {
        return transformedApplicationLocation;
    }

}
