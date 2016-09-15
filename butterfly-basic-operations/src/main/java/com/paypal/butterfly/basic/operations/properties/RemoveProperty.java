package com.paypal.butterfly.basic.operations.properties;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.TOExecutionResult;

import java.io.*;
import java.util.Properties;

/**
 * Operation to remove a property from a properties file.
 *
 * @author facarvalho
 */
public class RemoveProperty extends TransformationOperation<RemoveProperty> {

    private static final String DESCRIPTION = "Remove property '%s' from file '%s'";

    private String propertyName;

    public RemoveProperty() {
    }

    /**
     * Operation to remove a property from a properties file.
     *
     * @param propertyName name to the property to be removed
     */
    public RemoveProperty(String propertyName) {
        setPropertyName(propertyName);
    }

    public RemoveProperty setPropertyName(String propertyName) {
        checkForBlankString("Property Name", propertyName);
        this.propertyName = propertyName;
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, propertyName, getRelativePath());
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        boolean containsKey = false;
        TOExecutionResult result = null;
        try {
            File propertiesFile = getAbsoluteFile(transformedAppFolder, transformationContext);
            Properties properties = new Properties();
            fileInputStream = new FileInputStream(propertiesFile);
            properties.load(fileInputStream);
            containsKey = properties.getProperty(propertyName) != null;
            if (containsKey) {
                properties.remove(propertyName);
                fileOutputStream = new FileOutputStream(propertiesFile);
                properties.store(fileOutputStream, null);
            }
            String details;
            if (containsKey) {
                details = String.format("Property '%s' has been removed from '%s'", propertyName, getRelativePath());
            } else {
                details = String.format("Property '%s' has NOT been removed from '%s' because it is not present on it", propertyName, getRelativePath());
            }
            result = TOExecutionResult.success(this, details);
        } catch (IOException e) {
            result = TOExecutionResult.error(this, e);
        } finally {
            try {
                if (fileInputStream != null) try {
                    fileInputStream.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            } finally {
                if(fileOutputStream != null) try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            }
        }

        return result;
    }

    @Override
    public RemoveProperty clone() throws CloneNotSupportedException {
        RemoveProperty clonedRemoveProperty = (RemoveProperty) super.clone();
        return clonedRemoveProperty;
    }

}
