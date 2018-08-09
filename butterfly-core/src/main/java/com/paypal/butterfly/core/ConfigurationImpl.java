package com.paypal.butterfly.core;

import java.io.File;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.paypal.butterfly.api.Configuration;

/**
 * Butterfly configuration object
 *
 * @author facarvalho
 */
class ConfigurationImpl implements Configuration {

    // See the setters for information about each property
    private File outputFolder = null;
    private boolean zipOutput = false;
    private boolean modifyOriginalFolder = true;

    /**
     * Creates and returns a new {@link Configuration} object
     * set to apply the transformation against the original application folder
     * and the result will not be compressed to a zip file.
     * <br>
     * Notice that calling this method will result in {@link Configuration#isModifyOriginalFolder()}
     * to return {@code true}.
     *
     * @return a brand new {@link Configuration} object
     */
    ConfigurationImpl() {
    }

    /**
     * Creates and returns a new {@link Configuration} object
     * set to place the transformed application at a new folder at the original application
     * parent folder, besides compressing it to a zip file, depending on {@code zipOutput}.
     * <br>
     * The transformed application folder's name is the same as original folder,
     * plus a "-transformed-yyyyMMddHHmmssSSS" suffix.
     * <br>
     * Notice that calling this method will result in {@link Configuration#isModifyOriginalFolder()}
     * to return {@code false}.
     *
     * @param zipOutput if true, the transformed application folder will be compressed into a zip file
     * @return a brand new {@link Configuration} object
     */
    ConfigurationImpl(boolean zipOutput) {
        this.zipOutput = zipOutput;
        modifyOriginalFolder = false;
    }

    /**
     * Creates and returns a new {@link Configuration} object
     * set to place the transformed application at {@code outputFolder},
     * and compress it to a zip file or not, depending on {@code zipOutput}.
     * <br>
     * Notice that calling this method will result in {@link Configuration#isModifyOriginalFolder()}
     * to return {@code false}.
     *
     * @param outputFolder the output folder where the transformed application is
     *                     supposed to be placed
     * @param zipOutput if true, the transformed application folder will be compressed into a zip file
     * @return a brand new {@link Configuration} object
     * @throws IllegalArgumentException if {@code outputFolder} is null, does not exist, or is not a directory
     */
    ConfigurationImpl(File outputFolder, boolean zipOutput) {
        if(outputFolder == null) {
            throw new IllegalArgumentException(String.format("Output folder object cannot be null"));
        }
        if(!outputFolder.exists()) {
            throw new IllegalArgumentException(String.format("Output folder does not exist %s", outputFolder.getAbsolutePath()));
        }
        if(!outputFolder.isDirectory()) {
            throw new IllegalArgumentException(String.format("Output folder is not a directory %s", outputFolder.getAbsolutePath()));
        }
        this.outputFolder = outputFolder;
        this.zipOutput = zipOutput;
        modifyOriginalFolder = false;
    }

    @Override
    public File getOutputFolder() {
        return outputFolder;
    }

    @Override
    public boolean isZipOutput() {
        return zipOutput;
    }

    @Override
    public boolean isModifyOriginalFolder() {
        return modifyOriginalFolder;
    }

    @Override
    public String toString() {
        return String.format("{ outputFolder: %s , zipOutput: %s, modifyOriginalFolder: %s}", outputFolder, zipOutput, modifyOriginalFolder);
    }

    @Override
    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ConfigurationImpl)) {
            return false;
        }
        if (!ConfigurationImpl.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Configuration configuration = (Configuration)obj;
        if(this.zipOutput != configuration.isZipOutput()) {
            return false;
        }
        if(this.modifyOriginalFolder != configuration.isModifyOriginalFolder()) {
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
        return new HashCodeBuilder().append(this.outputFolder).append(this.zipOutput).append(this.modifyOriginalFolder).toHashCode();
    }

}
