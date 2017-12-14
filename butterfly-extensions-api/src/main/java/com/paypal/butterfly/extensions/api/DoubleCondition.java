package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Transformation utility condition to determine if a transformation utility
 * should be executed or not, based on a two files criteria. Every
 * {@code DoubleUtilityCondition} subclass result type must always
 * be boolean. The criteria to this type of condition
 * is based on two files (when comparing if two XML files are equal
 * for example). For conditions
 * based on evaluating a single file see {@link SingleCondition}.
 * For conditions based on multiple files see {@link MultipleConditions}
 * <br>
 * <strong>Important:</strong> it returns true if both files don't exist,
 * and it returns false if only one of them exists.
 *
 * @author facarvalho
 * @see SingleCondition
 * @see MultipleConditions
 */
public abstract class DoubleCondition<T extends DoubleCondition> extends UtilityCondition<T> {

    // The name of the transformation context attribute
    // that refers to the file to be compared against the baseline file
    private String attribute;

    // TODO
    // Rename attribute to compareAttribute

    // TODO
    // Add a new instance variable called compareRelative, to be used as an alternative to compareAttribute,
    // pointing directly to the comparison file, relative to the transformed application folder,
    // and without the need for a transformation context attribute.

    /**
     * Condition to determine if a transformation utility
     * should be executed or not. Every
     * DoubleUtilityCondition subclass result type must always
     * be boolean. The criteria to this type of condition
     * is based on two files (when comparing if two XML files are equal
     * for example)
     */
    public DoubleCondition() {
    }

    /**
     * Condition to determine if a transformation utility
     * should be executed or not. Every
     * DoubleUtilityCondition subclass result type must always
     * be boolean. The criteria to this type of condition
     * is based on two files (when comparing if two XML files are equal
     * for example)
     *
     * @param attribute the name of the transformation context attribute
     *                  that refers to the file to be compared against the baseline file
     */
    public DoubleCondition(String attribute) {
        setAttribute(attribute);
    }

    /**
     * Return the name of the transformation context attribute
     * that refers to the file to be compared against the
     * baseline file
     *
     * @return the name of the transformation context attribute
     * that refers to the file to be compared against the
     * baseline file
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Set the name of the transformation context attribute
     * that refers to the file to be compared against the
     * baseline file, which is set by regular {@link com.paypal.butterfly.extensions.api.TransformationUtility}
     * methods, like {@link #relative(String)} or {@link #absolute(String)}
     *
     * @param attribute the name of the transformation context attribute
     *                  that refers to the file to be compared against the baseline file
     * @return this utility condition instance
     */
    public T setAttribute(String attribute) {
        checkForBlankString("attribute", attribute);
        this.attribute = attribute;
        return (T) this;
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        try {
            File baselineFile = getAbsoluteFile(transformedAppFolder, transformationContext);
            File comparisonFile = getComparisonFile(transformationContext);

            boolean result = false;
            if (baselineFile.exists() && comparisonFile.exists()) {
                result = compare(baselineFile, comparisonFile);
            } else if (!baselineFile.exists() && !comparisonFile.exists()) {
                result = true;
            }

            return TUExecutionResult.value(this, result);
        } catch (TransformationUtilityException e) {
            return TUExecutionResult.error(this, e);
        }
    }

    /**
     * Returns true only if the compared files meet the comparison
     * criteria established and implemented by the subclass
     *
     * @param baselineFile   the baseline file used for comparison
     * @param comparisonFile the file to be compared against the baseline file
     * @return this utility condition instance
     */
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
