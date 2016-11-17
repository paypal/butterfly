package com.paypal.butterfly.core;

import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.TransformationResult;

import java.io.File;

/**
 * The transformation result implementation
 *
 * @author facarvalho
 */
class TransformationResultImpl implements TransformationResult {

    private Configuration configuration;
    private File transformedApplicationLocation;
    private File manualInstructionsFile;

    TransformationResultImpl(Configuration configuration, File transformedApplicationLocation, File manualInstructionsFile) {
        setConfiguration(configuration);
        setTransformedApplicationLocation(transformedApplicationLocation);
        setManualInstructionsFile(manualInstructionsFile);
    }

    private void setConfiguration(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration object cannot be null");
        }
        this.configuration = configuration;
    }

    private void setTransformedApplicationLocation(File transformedApplicationLocation) {
        if (transformedApplicationLocation == null) {
            throw new IllegalArgumentException("Transformed application location object cannot be null");
        }
        this.transformedApplicationLocation = transformedApplicationLocation;
    }

    private void setManualInstructionsFile(File manualInstructionsFile) {
        this.manualInstructionsFile = manualInstructionsFile;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public File getTransformedApplicationLocation() {
        return transformedApplicationLocation;
    }

    @Override
    public boolean hasManualInstructions() {
        return manualInstructionsFile != null;
    }

    @Override
    public File getManualInstructionsFile() {
        return manualInstructionsFile;
    }
}
