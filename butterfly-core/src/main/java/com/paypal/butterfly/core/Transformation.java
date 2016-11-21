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

    // Text file containing main manual instructions document (if any)
    private File manualInstructionsFile;

    // Directory containing manual instructions documents (if any)
    private File manualInstructionsDir;

    Transformation(Application application, Configuration configuration) {
        this.application = application;
        this.configuration = configuration;
    }

    void setTransformedApplicationLocation(File transformedApplicationLocation) {
        this.transformedApplicationLocation = transformedApplicationLocation;
    }

    void setManualInstructionsFile(File manualInstructionsFile) {
        this.manualInstructionsFile = manualInstructionsFile;
    }

    void setManualInstructionsDir(File manualInstructionsDir) {
        this.manualInstructionsDir = manualInstructionsDir;
    }

    Application getApplication() {
        return application;
    }

    Configuration getConfiguration() {
        return configuration;
    }

    File getTransformedApplicationLocation() {
        return transformedApplicationLocation;
    }

    File getManualInstructionsFile() {
        return manualInstructionsFile;
    }

    File getManualInstructionsDir() {
        return manualInstructionsDir;
    }

}
