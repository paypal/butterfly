package com.paypal.butterfly.basic.operations.properties;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            File propertiesFile = getAbsoluteFile(transformedAppFolder, transformationContext);
            Properties properties = new Properties();
            fileInputStream = new FileInputStream(propertiesFile);
            properties.load(fileInputStream);
            properties.put(propertyName, propertyValue);
            fileOutputStream = new FileOutputStream(propertiesFile);
            properties.store(fileOutputStream, null);
        } finally {
            try {
                if(fileInputStream != null) fileInputStream.close();
            } finally {
                if(fileOutputStream != null) fileOutputStream.close();
            }
        }

        return String.format("Property '%s' set to '%s' at '%s'", propertyName, propertyValue, getRelativePath());
    }

    @Override
    public AddProperty clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}
