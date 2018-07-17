package com.paypal.butterfly.core;

import com.google.common.io.Files;
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

    // The location where the baseline application code is
    // The baseline application is a copy of the original application, used only for
    // blank transformations
    private File baselineApplicationLocation = null;

    // Text file containing main manual instructions document (if any)
    private File manualInstructionsFile;

    // Directory containing manual instructions documents (if any)
    private File manualInstructionsDir;

    // Whether the transformation template set in this transformation object
    // is a "blank transformation" or not
    private boolean blank;

    Transformation(Application application, Configuration configuration, boolean blank) {
        this.application = application;
        this.configuration = configuration;
        setBlank(blank);
    }

    private void setBlank(boolean blank) {
        this.blank = blank;
        if (blank) {
            if (configuration.isModifyOriginalFolder()) {
                baselineApplicationLocation = Files.createTempDir();
                logger.debug("Baseline directory pointing to temporary directory: {}", baselineApplicationLocation.getAbsolutePath());
            } else {
                baselineApplicationLocation = application.getFolder();
                logger.debug("Baseline directory pointing to original application folder: {}", baselineApplicationLocation.getAbsolutePath());
            }
        }
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

    File getBaselineApplicationLocation() {
        return baselineApplicationLocation;
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
            logger.warn("An exception happened when retrieving extension version", e);
        }
        return version;
    }

    public boolean isBlank() {
        return blank;
    }

}
