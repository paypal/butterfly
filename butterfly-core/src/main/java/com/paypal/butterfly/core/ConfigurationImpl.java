package com.paypal.butterfly.core;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.paypal.butterfly.api.Configuration;

/**
 * Butterfly configuration object
 *
 * @author facarvalho
 */
class ConfigurationImpl implements Configuration {

    // See the setters for information about each property
    private Properties properties = null;
    private File outputFolder = null;
    private boolean zipOutput = false;
    private boolean modifyOriginalFolder = true;

    private static final Pattern propertyNameRegex = Pattern.compile("^[a-zA-Z][a-zA-Z0-9\\._-]*$");  

    /**
     * Creates and returns a new {@link Configuration} object
     * set to apply the transformation against the original application folder
     * and the result will not be compressed to a zip file.
     * <br>
     * Notice that calling this method will result in {@link Configuration#isModifyOriginalFolder()}
     * to return {@code true}.
     *
     * @param properties a properties object specifying details about the transformation itself.
     *                   These properties help to specialize the
     *                   transformation, for example, determining if certain operations should
     *                   be skipped or not, or how certain aspects of the transformation should
     *                   be executed. The set of possible properties is defined by the used transformation
     *                   extension and template, read the documentation offered by the extension
     *                   for further details. The properties values are defined by the user requesting the transformation.
     *                   Properties are optional, so, if not desired, this parameter can be set to null.
     * @return a brand new {@link Configuration} object
     * @throws IllegalArgumentException if properties object is invalid. Properties name must
     *                   be non blank and the first character must be an alphabet and the others may
     *                   be alphabetic, numeric, dot, underscore, or hyphen.  Properties value
     *                   must be Strings and cannot be null.
     */
    ConfigurationImpl(Properties properties) {
        if (properties != null && properties.size() > 0) {

            // Validating properties object
            List<String> invalidProperties = properties.entrySet().stream()
                    .filter(e -> !propertyNameRegex.matcher((String) e.getKey()).matches() || !(e.getValue() instanceof String))
                    .map(e -> (String) e.getKey())
                    .collect(Collectors.toList());
            if (!invalidProperties.isEmpty()) {
                throw new IllegalArgumentException("The following properties are invalid: " + invalidProperties);
            }

            this.properties = properties;
        }
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
     * @param properties a properties object specifying details about the transformation itself.
     *                   These properties help to specialize the
     *                   transformation, for example, determining if certain operations should
     *                   be skipped or not, or how certain aspects of the transformation should
     *                   be executed. The set of possible properties is defined by the used transformation
     *                   extension and template, read the documentation offered by the extension
     *                   for further details. The properties values are defined by the user requesting the transformation.
     *                   Properties are optional, so, if not desired, this parameter can be set to null.
     * @param zipOutput if true, the transformed application folder will be compressed into a zip file
     * @return a brand new {@link Configuration} object
     */
    ConfigurationImpl(Properties properties, boolean zipOutput) {
        this(properties);
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
     * @param properties a properties object specifying details about the transformation itself.
     *                   These properties help to specialize the
     *                   transformation, for example, determining if certain operations should
     *                   be skipped or not, or how certain aspects of the transformation should
     *                   be executed. The set of possible properties is defined by the used transformation
     *                   extension and template, read the documentation offered by the extension
     *                   for further details. The properties values are defined by the user requesting the transformation.
     *                   Properties are optional, so, if not desired, this parameter can be set to null.
     * @param outputFolder the output folder where the transformed application is
     *                     supposed to be placed
     * @param zipOutput if true, the transformed application folder will be compressed into a zip file
     * @return a brand new {@link Configuration} object
     * @throws IllegalArgumentException if {@code outputFolder} is null, does not exist, or is not a directory
     */
    ConfigurationImpl(Properties properties, File outputFolder, boolean zipOutput) {
        this(properties);
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
    public Properties getProperties() {
        return properties;
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
        return String.format("{ properties: %s, outputFolder: %s, zipOutput: %s, modifyOriginalFolder: %s}", properties, outputFolder, zipOutput, modifyOriginalFolder);
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
