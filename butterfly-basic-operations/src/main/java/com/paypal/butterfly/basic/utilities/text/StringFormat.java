package com.paypal.butterfly.basic.utilities.text;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;

import java.io.File;
import java.util.Arrays;

/**
 * This utility register a new transformation context
 * attribute by applying one or more existent
 * String transformation context attributes to
 * {@link String#format(String, Object...)}.
 * The setting order of attributes will be
 * honored when applying the formatting.
 *
 * @author facarvalho
 */
public class StringFormat extends TransformationUtility<StringFormat, String> {

    private static final String DESCRIPTION = "Apply transformation context attributes %s to '%s'";

    private String format;
    private String[] attributeNames;

    public StringFormat() {
    }

    public StringFormat(String format) {
        setFormat(format);
    }

    public StringFormat setFormat(String format) {
        this.format = format;
        return this;
    }

    public StringFormat setAttributeNames(String... attributeNames) {
        this.attributeNames = attributeNames;
        return this;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, Arrays.toString(attributeNames), format);
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        String[] attributeValues = new String[attributeNames.length];
        String attributeName;
        for (int i = 0; i < attributeNames.length; i++) {
            attributeValues[i] = (String) transformationContext.get(attributeNames[i]);
        }
        return String.format(format, attributeValues);
    }

}
