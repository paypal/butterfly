package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Transformation utility to filter a list of files based on a given
 * {@link com.paypal.butterfly.extensions.api.SingleCondition},
 * returning in a sub-list of files.
 *
 * @author facarvalho
 */
public class FilterFiles extends TransformationUtility<FilterFiles> {

    private static final Logger logger = LoggerFactory.getLogger(FilterFiles.class);

    private static final String DESCRIPTION = "Given a list of files and one condition, returns a sub-list containing all files that meet that condition";

    // Array of transformation context attributes that hold list of Files
    // which the single condition should be evaluated against.
    // If more than one attribute is specified, all list of files will be
    // combined into a single one.
    private String[] filesAttributes;

    // Single condition to be evaluated against all files
    private SingleCondition conditionTemplate;

    // This is used to set condition instances names
    private int conditionInstanceCounter = 0;

    /**
     * Utility to filter a list of files based on a given
     * {@link com.paypal.butterfly.extensions.api.SingleCondition},
     * returning in a sub-list of files
     */
    public FilterFiles() {
    }

    /**
     * Utility to filter a list of files based on a given
     * {@link com.paypal.butterfly.extensions.api.SingleCondition},
     * returning in a sub-list of files
     *
     * @param conditionTemplate the single condition template to be evaluated against all files
     */
    public FilterFiles(SingleCondition conditionTemplate) {
        setConditionTemplate(conditionTemplate);
    }

    /**
     * Sets one or more transformation context attributes that hold list of Files
     * which the single condition should be evaluated against.
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
     * Set the single condition template to be evaluated against all files
     *
     * @param conditionTemplate the single condition template to be evaluated against all files
     * @return this transformation utility object
     */
    public FilterFiles setConditionTemplate(SingleCondition conditionTemplate) {
        checkForNull("conditionTemplate", conditionTemplate);
        this.conditionTemplate = conditionTemplate;
        return this;
    }

    @Override
    protected FilterFiles setName(String name) {
        conditionTemplate.setName(String.format("%s-%s-TEMPLATE_CONDITION", name, conditionTemplate.getClass().getSimpleName()));
        return super.setName(name);
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
     * Return the single condition template to be evaluated against all files
     *
     * @return the single condition template to be evaluated against all files
     */
    public SingleCondition getConditionTemplate() {
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
     * Creates a new single condition instance copying from this current
     * object, but setting the file it should perform against based
     * on the input parameters
     *
     * @param transformedAppFolder the transformed application folder
     * @param file the actual file to be performed against
     * @return this transformation utility instance
     */
    public SingleCondition newConditionInstance(File transformedAppFolder, File file) {
        SingleCondition condition = (SingleCondition) conditionTemplate.copy();
        condition.relative(TransformationUtility.getRelativePath(transformedAppFolder, file));
        condition.setSaveResult(false);

        conditionInstanceCounter++;
        condition.setName(String.format("%s-%d", conditionTemplate.getName(), conditionInstanceCounter));

        return condition;
    }

}
