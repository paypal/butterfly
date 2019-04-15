package com.paypal.butterfly.api;

import java.io.File;
import java.util.Properties;

/**
 * Butterfly transformation configuration object. This object specify configuration
 * details about the requested transformation.
 * Use one of the factory methods under {@link ButterflyFacade} to create a new configuration object.
 *
 * @author facarvalho
 */
public interface Configuration {

    /**
     * Returns the folder where the transformed application is supposed to be placed,
     * or null, if no custom folder has been specified
     *
     * @return the folder where the transformed application is supposed to be placed
     */
    File getOutputFolder();

    /**
     * Returns whether the transformed application folder will be compressed into a zip file or not
     *
     * @return whether the transformed application folder will be compressed into a zip file or not
     */
    boolean isZipOutput();

    /**
     * Returns whether the transformation will occur in the original application folder
     *
     * @return whether the transformation will occur in the original application folder
     */
    boolean isModifyOriginalFolder();

    /**
     * Returns a properties object specifying details about the transformation itself.
     * These properties help to specialize the transformation, for example,
     * determining if certain operations should be skipped or not,
     * or how certain aspects of the transformation should be executed.
     * <br>
     * The set of possible properties is defined by the used transformation
     * extension and template, read the documentation offered by the extension
     * for further details.
     * <br>
     * The properties values are defined by the user requesting the transformation.
     * <br>
     * Properties are optional and, if not set, this method will return null.
     *
     * @return transformation request specific properties
     */
    Properties getProperties();

}
