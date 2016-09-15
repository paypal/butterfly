package com.paypal.butterfly.basic.operations.properties;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.TOExecutionResult;

import java.io.*;
import java.util.Properties;

/**
 * Operation to add a new property to a properties file.
 * If the property already exists, its value is overwritten.
 *
 * @author facarvalho
 */
public class AddProperty extends TransformationOperation<AddProperty> {

    private static final String DESCRIPTION = "Add new property ('%s' = '%s') to file '%s'";

    private String propertyName;
    private String propertyValue;

    public AddProperty() {
    }

    /**
     * Operation to add a new property to a properties file.
     * If the property already exists, its value is overwritten.
     *
     * @param propertyName name to the property to be added
     * @param propertyValue value to be set to the new property
     */
    public AddProperty(String propertyName, String propertyValue) {
        setPropertyName(propertyName);
        setPropertyValue(propertyValue);
    }

    public AddProperty setPropertyName(String propertyName) {
        checkForBlankString("Property Name", propertyName);
        this.propertyName = propertyName;
        return this;
    }

    public AddProperty setPropertyValue(String propertyValue) {
        checkForBlankString("Property Value", propertyValue);
        this.propertyValue = propertyValue;
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, propertyName, propertyValue, getRelativePath());
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        TOExecutionResult result = null;
        try {
            File propertiesFile = getAbsoluteFile(transformedAppFolder, transformationContext);
            Properties properties = new Properties();
            fileInputStream = new FileInputStream(propertiesFile);
            properties.load(fileInputStream);
            properties.put(propertyName, propertyValue);
            fileOutputStream = new FileOutputStream(propertiesFile);
            properties.store(fileOutputStream, null);

            String details = String.format("Property '%s' set to '%s' at '%s'", propertyName, propertyValue, getRelativePath());
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
    public AddProperty clone() throws CloneNotSupportedException {
        AddProperty clonedAddProperty = (AddProperty) super.clone();
        return clonedAddProperty;
    }

}
