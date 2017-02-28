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

    private Transformation transformation;
    private File transformedApplicationLocation;

    TransformationResultImpl(Transformation transformation, File transformedApplicationLocation) {
        setTransformation(transformation);
        setTransformedApplicationLocation(transformedApplicationLocation);
    }

    private void setTransformation(Transformation transformation) {
        if (transformation == null) {
            throw new IllegalArgumentException("Transformation object cannot be null");
        }
        this.transformation = transformation;
    }

    private void setTransformedApplicationLocation(File transformedApplicationLocation) {
        if (transformedApplicationLocation == null) {
            throw new IllegalArgumentException("Transformed application location object cannot be null");
        }
        this.transformedApplicationLocation = transformedApplicationLocation;
    }

    @Override
    public Configuration getConfiguration() {
        return transformation.getConfiguration();
    }

    @Override
    public File getTransformedApplicationLocation() {
        return transformedApplicationLocation;
    }

    @Override
    public boolean hasManualInstructions() {
        return transformation.getManualInstructionsFile() != null;
    }

    @Override
    public File getManualInstructionsFile() {
        return transformation.getManualInstructionsFile();
    }
}
