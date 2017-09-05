package com.paypal.butterfly.utilities.text;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;

import java.io.File;
import java.util.Arrays;

/**
 * Registers a new transformation context
 * attribute by applying one or more existent
 * String transformation context attributes to
 * {@link String#format(String, Object...)}.
 * The setting order of attributes will be
 * honored when applying the formatting.
 *
 * @author facarvalho
 */
public class StringFormat extends TransformationUtility<StringFormat> {

    private static final String DESCRIPTION = "Apply transformation context attributes %s to '%s'";

    private String format;
    private String[] attributeNames;

    public StringFormat() {
    }

    public StringFormat(String format) {
        setFormat(format);
    }

    public StringFormat setFormat(String format) {
        checkForBlankString("Format", format);
        this.format = format;
        return this;
    }

    public StringFormat setAttributeNames(String... attributeNames) {
        if(attributeNames == null || attributeNames.length == 0) {
            throw new TransformationDefinitionException("Attribute names cannot be null or empty");
        }
        this.attributeNames = attributeNames;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public String[] getAttributeNames() {
        return attributeNames.clone();
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, Arrays.toString(attributeNames), format);
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        TUExecutionResult result = null;

        if (attributeNames != null) {
            String[] attributeValues = new String[attributeNames.length];
            for (int i = 0; i < attributeNames.length; i++) {
                attributeValues[i] = (String) transformationContext.get(attributeNames[i]);
            }
            result = TUExecutionResult.value(this, String.format(format, attributeValues));
        } else {
            result = TUExecutionResult.warning(this, format, "No attributes have been specified");
        }

        return result;
    }

}
