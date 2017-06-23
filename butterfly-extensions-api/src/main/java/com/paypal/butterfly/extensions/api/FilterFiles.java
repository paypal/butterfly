package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Utility to filter a list of files based on a given
 * {@link com.paypal.butterfly.extensions.api.UtilityCondition},
 * returning in a sub-list of files
 *
 * @author facarvalho
 */
public class FilterFiles extends TransformationUtility<FilterFiles> {

    private static final Logger logger = LoggerFactory.getLogger(FilterFiles.class);

    private static final String DESCRIPTION = "Given a list of files and one condition, returns a sub-list containing all files that meet that condition";

    // Array of transformation context attributes that hold list of Files
    // which the condition should be evaluated against.
    // If more than one attribute is specified, all list of files will be
    // combined into a single one.
    private String[] filesAttributes;

    // Condition to be evaluated against all files
    private UtilityCondition conditionTemplate;

    /**
     * Utility to filter a list of files based on a given
     * {@link com.paypal.butterfly.extensions.api.UtilityCondition},
     * returning in a sub-list of files
     */
    public FilterFiles() {
    }

    /**
     * Utility to filter a list of files based on a given
     * {@link com.paypal.butterfly.extensions.api.UtilityCondition},
     * returning in a sub-list of files
     *
     * @param conditionTemplate the condition template to be evaluated against all files
     */
    public FilterFiles(UtilityCondition conditionTemplate) {
        setConditionTemplate(conditionTemplate);
    }

    /**
     * Sets one or more transformation context attributes that hold list of Files
     * which the condition should be evaluated against.
     * If more than one attribute is specified, all list of files will be
     * combined into a single one
     *
     * @param filesAttributes one or more transformation context attributes that hold list
     *                   of Files which the condition should be evaluated against
     * @return this transformation utility object
     */
    public FilterFiles setFiles(String... filesAttributes) {
        this.filesAttributes = filesAttributes;
        return this;
    }

    /**
     * Set the condition template to be evaluated against all files
     *
     * @param conditionTemplate the condition template to be evaluated against all files
     * @return this transformation utility object
     */
    public FilterFiles setConditionTemplate(UtilityCondition conditionTemplate) {
        this.conditionTemplate = conditionTemplate;
        return this;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Return an array containing the name of transformation context attributes
     * that hold the list of files to be filtered
     *
     * @return an array containing the name of transformation context attributes
     * that hold the list of files to be filtered
     */
    public String[] getFilesAttributes() {
        return Arrays.copyOf(filesAttributes, filesAttributes.length);
    }

    /**
     * Return the condition template to be evaluated against all files
     *
     * @return the condition template to be evaluated against all files
     */
    public UtilityCondition getConditionTemplate() {
        return conditionTemplate;
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {

        // TODO
        // This should be done during validation

        if (conditionTemplate == null) {
            TransformationUtilityException e = new TransformationUtilityException("No condition template has been specified");
            return TUExecutionResult.error(this, e);
        }

        Collection<File> files;
        Set<File> allFiles = new HashSet<>();

        for(String attribute: filesAttributes) {
            files = (Collection<File>) transformationContext.get(attribute);
            if (files != null) {
                allFiles.addAll(files);
            }
        }

        String details = null;
        if(logger.isDebugEnabled()) {
            details = String.format("FilterFiles %s resulted in %d files based on %s", getName(), allFiles.size(), conditionTemplate.getClass().getSimpleName());
        }
        return TUExecutionResult.value(this, allFiles).setDetails(details);
    }

    /**
     * Creates a new condition instance copying from this current
     * object, but setting the file it should perform against based
     * on the input parameters
     *
     * @param transformedAppFolder the transformed application folder
     * @param file the actual file to be performed against
     * @return
     */
    public UtilityCondition newConditionInstance(File transformedAppFolder, File file) {
        try {
            UtilityCondition condition = (UtilityCondition) conditionTemplate.copy();
            condition.relative(TransformationUtility.getRelativePath(transformedAppFolder, file));

            return condition;
        } catch (CloneNotSupportedException e) {
            String exceptionMessage = String.format("Error when preparing condition instance for %s", getName());
            throw new TransformationUtilityException(exceptionMessage, e);
        }
    }

}
