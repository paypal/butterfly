package com.paypal.butterfly.basic.operations.properties;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        boolean containsKey = false;
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
        } finally {
            try {
                if(fileInputStream != null) fileInputStream.close();
            } finally {
                if(fileOutputStream != null) fileOutputStream.close();
            }
        }

        String result;
        if (containsKey) {
            result = String.format("Property '%s' has been removed from '%s'", propertyName, getRelativePath());
        } else {
            result = String.format("Property '%s' has NOT been removed from '%s' because it is not present on it", propertyName, getRelativePath());
        }

        return result;
    }

    @Override
    public RemoveProperty clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}
