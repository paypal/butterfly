package com.paypal.butterfly.basic.operations.properties;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Operation to add a new property to a properties file
 *
 * @author facarvalho
 */
public class AddProperty extends TransformationOperation<AddProperty> {

    private static final String DESCRIPTION = "Add new property (%s = %s) to file %s";

    private String propertyName;
    private String propertyValue;

    public AddProperty() {
    }

    /**
     * Operation to add a new property to a properties file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     */
    private AddProperty(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to add a new property to a properties file
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     * @param propertyName name to the property to be added
     * @param propertyValue value to be set to the new property
     */
    public AddProperty(String relativePath, String propertyName, String propertyValue) {
        this(relativePath);
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public AddProperty setPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    public AddProperty setPropertyValue(String propertyValue) {
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
        // TODO

        return null;
    }

}
