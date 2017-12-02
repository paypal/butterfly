package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.SingleCondition;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Checks if a particular property exists in a property file.
 * The property name can be defined explicitly, via {@link #setPropertyName(String)},
 * or as a regular expression, via {@link #setPropertyNameRegex(String)}.
 *
 * @author facarvalho
 */
public class PropertyExists extends SingleCondition<PropertyExists> {

    private static final String DESCRIPTION = "Check if property '%s' exists in a property file";

    private String propertyName;
    private String propertyNameRegex;

    public PropertyExists() {
    }

    public PropertyExists(String propertyName) {
        setPropertyName(propertyName);
    }

    public PropertyExists setPropertyName(String propertyName) {
        checkForBlankString("propertyName", propertyName);
        this.propertyName = propertyName;
        return this;
    }

    public PropertyExists setPropertyNameRegex(String propertyNameRegex) {
        checkForBlankString("propertyNameRegex", propertyNameRegex);
        this.propertyNameRegex = propertyNameRegex;
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyNameRegex() {
        return propertyNameRegex;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, (propertyName != null ? propertyName : propertyNameRegex));
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {

        // TODO
        // Move this to pre-validation method
        if (propertyName == null && propertyNameRegex == null) {
            throw new TransformationUtilityException("Property name neither property name regex were defined");
        }

        FileInputStream fileInputStream = null;
        boolean exists = false;
        TransformationUtilityException ex = null;

        File file = getAbsoluteFile(transformedAppFolder, transformationContext);

        try {
            fileInputStream = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fileInputStream);

            if (propertyName != null) {
                exists = properties.getProperty(propertyName) != null;
            } else {
                Pattern pattern = Pattern.compile(propertyNameRegex);
                for (String pName : properties.stringPropertyNames()) {
                    if (pattern.matcher(pName).matches()) {
                        exists = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            String relativeFile = getRelativePath(transformedAppFolder, file);
            String details = String.format("Exception happened when checking if property %s exists in %s", propertyName, relativeFile);
            ex = new TransformationUtilityException(details, e);
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    if (ex == null) {
                        String relativeFile = getRelativePath(transformedAppFolder, file);
                        ex = new TransformationUtilityException("Exception happened when closing properties file " + relativeFile, e);
                    } else {
                        ex.addSuppressed(e);
                    }
                }
            }
        }

        if (ex != null) {
            return TUExecutionResult.error(this, ex);
        }

        return TUExecutionResult.value(this, exists);
    }

}
