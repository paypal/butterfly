package com.paypal.butterfly.core;

import java.io.File;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.paypal.butterfly.facade.Configuration;

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
     * Butterfly default configuration
     */
    ConfigurationImpl() {
    }

    @Override
    public Configuration setOutputFolder(File outputFolder) {
        if(outputFolder != null && !outputFolder.exists()) {
            throw new IllegalArgumentException(String.format("Invalid application folder, it does not exist %s", outputFolder));
        }
        if(outputFolder != null && !outputFolder.isDirectory()) {
            throw new IllegalArgumentException(String.format("Invalid application folder, that is not a directory %s", outputFolder));
        }
        this.outputFolder = outputFolder;
        this.modifyOriginalFolder = false;
        return this;
    }

    @Override
    public Configuration setZipOutput(boolean zipOutput) {
        this.zipOutput = zipOutput;
        return this;
    }

    @Override
    public Configuration setModifyOriginalFolder(boolean modifyOriginalFolder) {
        this.modifyOriginalFolder = modifyOriginalFolder;
        if (modifyOriginalFolder) {
            this.outputFolder = null;
            this.zipOutput = false;
        }
        return this;
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
