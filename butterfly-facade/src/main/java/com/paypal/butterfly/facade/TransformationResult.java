package com.paypal.butterfly.facade;

import java.io.File;

/**
 * Transformation result
 *
 * @author facarvalho
 */
public interface TransformationResult {

    /**
     * Returns the configuration object associated with this transformation
     *
     * @return the configuration object associated with this transformation
     */
    Configuration getConfiguration();

    /**
     * The folder where the transformed application is
     *
     * @return the folder where the transformed application is
     */
    File getTransformedApplicationLocation();

    /**
     * Returns true if this transformation requires
     * manual instructions to be completed
     *
     * @return true if this transformation requires
     * manual instructions to be completed
     */
    boolean hasManualInstructions();

    /**
     * Returns the document file that contains
     * the manual instructions, or null, if here is none
     *
     * @return the document file that contains
     * the manual instructions, or null, if here is none
     */
    File getManualInstructionsFile();

}
