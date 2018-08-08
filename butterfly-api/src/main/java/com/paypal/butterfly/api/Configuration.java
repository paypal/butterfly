package com.paypal.butterfly.api;

import java.io.File;

/**
 * Butterfly transformation configuration object.
 * This POJO holds user input details about the transformation request.
 *
 * @author facarvalho
 */
public interface Configuration {

    /**
     * Return the folder where the transformed application is supposed to be placed
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
     * Returns whether the transformation will occur in application folder
     *
     * @return whether the transformation will occur in application folder
     */
    boolean isModifyOriginalFolder();

}
