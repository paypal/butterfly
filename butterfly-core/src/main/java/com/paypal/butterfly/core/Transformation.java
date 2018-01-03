package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.facade.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Represents an specific transformation to be applied
 * against a specific application
 *
 * @author facarvalho
 */
@SuppressWarnings("PMD.DefaultPackage")
public abstract class Transformation {

    private static final Logger logger = LoggerFactory.getLogger(Transformation.class);

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

    abstract String getExtensionName();

    abstract String getExtensionVersion();

    abstract String getTemplateName();

    protected String getExtensionName(Class<? extends Extension> extension) {
        return extension.getName();
    }

    protected String getExtensionVersion(Class<? extends Extension> extension) {
        String version = "UNKNOWN";
        try {
            version = extension.newInstance().getVersion();
        } catch (Exception e) {
            logger.warn("Error happened when retrieving extension version", e);
        }
        return version;
    }

}
