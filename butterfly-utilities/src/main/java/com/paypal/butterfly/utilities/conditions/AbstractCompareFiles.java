package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.ExecutionResult;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.UtilityCondition;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This abstract utility condition compares two files and returns true only
 * if their contents are equal. The first file is the one specified
 * by {@link #relative(String)} or {@link #absolute(String)}, while the
 * second one is specified as a String containing the name of the
 * transformation context attribute that holds the file.
 * </br>
 * It is up to its subclasses to perform the actual comparison logic
 * and define what "equals" means.
 *
 * @author facarvalho
 */
public abstract class AbstractCompareFiles<T> extends UtilityCondition<T> {

    private static final String DESCRIPTION = "Compare file %s to another one, return true only if their contents are equal";

    private String attribute;

    public AbstractCompareFiles() {
    }

    public T setAttribute(String attribute) {
        checkForBlankString("attribute", attribute);
        this.attribute = attribute;
        return (T) this;
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        try {
            File baselineFile = getAbsoluteFile(transformedAppFolder, transformationContext);
            File comparisonFile = getComparisonFile(transformationContext);

            boolean result = compare(baselineFile, comparisonFile);

            return TUExecutionResult.value(this, result);
        } catch (TransformationUtilityException e) {
            return TUExecutionResult.error(this, e);
        }
    }

    protected abstract boolean compare(File baselineFile, File comparisonFile);

    private File getComparisonFile(TransformationContext transformationContext) throws TransformationUtilityException {
        if (attribute == null || StringUtils.isBlank(attribute)) {
            throw new TransformationUtilityException("Attribute name has not been set");
        }
        File comparisonFile = (File) transformationContext.get(attribute);
        if (comparisonFile == null) {
            String exceptionMessage = String.format("Comparison file from attribute %s is null", attribute);
            throw new TransformationUtilityException(exceptionMessage);
        }

        return comparisonFile;
    }

}
