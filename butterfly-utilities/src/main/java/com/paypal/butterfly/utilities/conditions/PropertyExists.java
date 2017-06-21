package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Condition to check if a particular property exists in one
 * or more properties files. Multiple files can be  specified (via {@link #setFiles(String...)}).
 * While one file can be specified by regular {@link #relative(String)} and
 * {@link #absolute(String)} methods.
 * </br>
 * When evaluating multiple files, the criteria is configurable, and
 * can be all of them have the property, or at least one of them has it.
 * </br>
 * These three behaviors are set by the following methods:
 * <ol>
 *     <li>{@link #singleFile()}</li>
 *     <li>{@link #multipleFilesAtLeastOne()}</li>
 *     <li>{@link #multipleFilesAll()}</li>
 * </ol>
 * <strong>Notes:</strong>
 * <ol>
 *     <li>{@link #singleFile()} is the default behavior if none is specified</li>
 *     <li>When a multiple files method is set, the file specified by {@link #relative(String)}
 *     or {@link #absolute(String)} is ignored</li>
 * </ol>
 * </br>
 * The property name can be defined explicitly, via {@link #setPropertyName(String)},
 * or as a regular expression, via {@link #setPropertyNameRegex(String)}.
 * </br>
 *
 * @author facarvalho
 */
public class PropertyExists extends AbstractCriteriaCondition<PropertyExists> {

    private static final String DESCRIPTION = "Check if property '%s' exists in one ore more properties files";

    private String propertyName;
    private String propertyNameRegex;

    public PropertyExists() {
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
    protected boolean eval(File transformedAppFolder, TransformationContext transformationContext, File file) {

        // TODO
        // Move this to pre-validation method
        if (propertyName == null && propertyNameRegex == null) {
            throw new TransformationUtilityException("Property name neither property name regex were defined");
        }

        FileInputStream fileInputStream = null;
        boolean exists = false;
        TransformationUtilityException ex = null;

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
            throw ex;
        }

        return exists;
    }

}
