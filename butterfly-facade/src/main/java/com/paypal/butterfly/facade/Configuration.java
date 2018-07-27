package com.paypal.butterfly.facade;

import java.io.File;

/**
 * Butterfly transformation configuration object.
 * This POJO holds user input details about the transformation request.
 * <br>
 * Unless set otherwise explicitly, a brand new Configuration object
 * will apply the transformation against the original application folder
 * and the result won't be compressed to a zip file.
 *
 * @author facarvalho
 */
public interface Configuration {

    /**
     * The folder location in the file system where the transformed application
     * should be placed.
     * <br>
     * If null, it defaults to same location where original application is.
     * In this case the transformed application is placed under a new folder
     * whose named is same as original folder, plus a "-transformed-yyyyMMddHHmmssSSS"
     * suffix.
     * Notice that calling this method will result in {@link #isModifyOriginalFolder()}
     * to return {@code false}.
     *
     * @param outputFolder the output folder where the transformed application is
     *                     supposed to be placed
     */
    Configuration setOutputFolder(File outputFolder);

    /**
     * If set to true, the transformed application folder will be compressed
     * into a zip file, and the transformed folder will be removed. The zip
     * file will be named as the transformed application folder,
     * plus the zip extension.
     * Notice though that, if {@link #isModifyOriginalFolder()}
     * returns true, then this zip output flag will be ignored.
     *
     * @param zipOutput if true, the transformed application folder will be compressed into a zip file
     */
    Configuration setZipOutput(boolean zipOutput);

    /**
     * If set to true, the transformation will occur in the original application folder, and
     * values set at {@link #setOutputFolder(File)} and {@link #setZipOutput(boolean)} will be reset.
     *
     * @param modifyOriginalFolder if true, the transformation will occur in the original application folder
     */
    Configuration setModifyOriginalFolder(boolean modifyOriginalFolder);

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
