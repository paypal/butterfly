package com.paypal.butterfly.facade;

import org.apache.commons.lang3.builder.HashCodeBuilder;

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
     * Butterfly default configuration
     *
     * @param outputFolder the output folder where the transformed application is
     *                     supposed to be placed
     * @param zipOutput if true, the transformed application folder will be compressed into a zip file
     */
    public Configuration(File outputFolder, boolean zipOutput) {
        setOutputFolder(outputFolder);
        setZipOutput(zipOutput);
    }

    /**
     * The folder location in the file system where the transformed application
     * should be placed.
     * <br>
     * If null, it defaults to same location where original application is.
     * n this case the transformed application is placed under a new folder
     * whose named is same as original folder, plus a "-transformed-yyyyMMddHHmmssSSS"
     * suffix
     *
     * @param outputFolder the output folder where the transformed application is
     *                     supposed to be placed
     */
    public void setOutputFolder(File outputFolder) {
        if(outputFolder != null && (!outputFolder.exists() || !outputFolder.isDirectory())) {
            throw new IllegalArgumentException(String.format("Invalid application folder %s",outputFolder));
        }
        this.outputFolder = outputFolder;
    }

    /**
     * If set to true, the transformed application folder will be compressed
     * into a zip file, and the transformed folder will be removed. The zip
     * file will be named as the transformed application folder,
     * plus the zip extension
     *
     * @param zipOutput if true, the transformed application folder will be compressed into a zip file
     */
    public void setZipOutput(boolean zipOutput) {
        this.zipOutput = zipOutput;
    }

    /**
     * Return the folder where the transformed application is supposed to be placed
     *
     * @return the folder where the transformed application is supposed to be placed
     */
    public File getOutputFolder() {
        return outputFolder;
    }

    /**
     * Returns whether the transformed application folder will be compressed into a zip file or not
     *
     * @return whether the transformed application folder will be compressed into a zip file or not
     */
    public boolean isZipOutput() {
        return zipOutput;
    }

    @Override
    public String toString() {
        return String.format("{ outputFolder: %s , zipOutput: %s }", outputFolder, zipOutput);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Configuration)) {
            return false;
        }
        if (!Configuration.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Configuration configuration = (Configuration)obj;
        if(this.zipOutput != configuration.isZipOutput()) {
            return false;
        }
        if (this.outputFolder == null && configuration.getOutputFolder() != null) {
            return false;
        }
        if (this.outputFolder != null && configuration.getOutputFolder() == null) {
            return false;
        }
        if(this.outputFolder != null && configuration.getOutputFolder() != null && !this.outputFolder.equals(configuration.getOutputFolder())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.outputFolder).append(this.zipOutput).toHashCode();
    }

}
