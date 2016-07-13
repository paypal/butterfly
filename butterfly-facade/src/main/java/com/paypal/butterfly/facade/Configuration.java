package com.paypal.butterfly.facade;

import java.io.File;

/**
 * Butterfly configuration object
 *
 * @author facarvalho
 */
public class Configuration {

    // See the setters for information about each property
    private File outputFolder = null;
    private boolean zipOutput = false;

    /**
     * Butterfly default configuration
     */
    public Configuration() {
        // No-op
        // Just a default constructor allowing the default
        // configuration to take place
    }

    /**
     * @see {@link Configuration}
     * @see {@link #setOutputFolder(File)}
     * @see {@link #setZipOutput(boolean)}
     *
     * @param outputFolder
     * @param zipOutput
     */
    public Configuration(File outputFolder, boolean zipOutput) {
        this.outputFolder = outputFolder;
        this.zipOutput = zipOutput;
    }

    /**
     * The folder location in the file system where the transformed application
     * should be placed.
     * </br>
     * If null, it defaults to same location where original application is.
     * n this case the transformed application is placed under a new folder
     * whose named is same as original folder, plus a "-transformed-yyyyMMddHHmmssSSS"
     * suffix
     *
     * @param outputFolder the output folder where the transformed application is
     *                     supposed to be placed
     */
    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * If set to true, the transformed application folder will be compressed
     * to a zip file, and the transformed folder will be removed. The zip
     * file will be named as the transformed application folder,
     * plus the zip extension
     *
     * @param zipOutput
     */
    public void setZipOutput(boolean zipOutput) {
        this.zipOutput = zipOutput;
    }

    /**
     * @see {@link #setOutputFolder(File)}
     *
     * @return
     */
    public File getOutputFolder() {
        return outputFolder;
    }

    /**
     * @see {@link #setZipOutput(boolean)}
     *
     * @return
     */
    public boolean isZipOutput() {
        return zipOutput;
    }

    private static final String TO_STRING_FORMAT = "{ outputFolder: %s , zipOutput: %s }";

    @Override
    public String toString() {
        return String.format(TO_STRING_FORMAT, outputFolder, zipOutput);
    }
}
