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
 * Transformation utility condition to determine if a transformation utility should be executed or not,
 * based on a multiple files criteria.
 * It performs condition instances based on a {@link SingleCondition} template against multiple files,
 * returning true if at least one file meets the condition (default mode).
 * There is an alternative mode where all files need to meet the
 * evaluation condition to result in true. For conditions
 * based on comparing two files see {@link DoubleCondition}.
 * For conditions based on evaluating a single file see {@link SingleCondition}
 * <br>
 * Note 1: if an evaluation against a specific file fails for any reason, then the
 * overall evaluation will be interrupted and result also in a failure.
 * Note 2: if the utility condition object (to be executed against the specified files)
 * has conditions, they will be ignored.
 *
 * @see #setMode(Mode)
 * @see SingleCondition
 * @see DoubleCondition
 *
 * @author facarvalho
 */
public class MultipleConditions extends UtilityCondition<MultipleConditions> {

    private static final Logger logger = LoggerFactory.getLogger(MultipleConditions.class);

    private static final String DESCRIPTION = "Evaluate condition '%s' against multiple files";

    /**
     * Execution mode for {@link MultipleConditions}.
     */
    public enum Mode {

        /**
         * The {@link MultipleConditions} execution will returning true if AT LEAST ONE file meets the specified condition.
         * This is the default execution mode.
         */
        AT_LEAST_ONE,

        /**
         * The {@link MultipleConditions} execution will returning true only if ALL files meet the specified condition.
         */
        ALL
    }

    private Mode mode = Mode.AT_LEAST_ONE;

    // Array of transformation context attributes that hold list of Files
    // which the condition should perform against.
    // If more than one attribute is specified, all list of files will be
    // combined into a single one.
    private String[] filesAttributes;

    // The utility condition template used to create conditions
    // to  by evaluated against the list of files
    private SingleCondition conditionTemplate;

    // This is used to set condition instances names
    private int conditionInstanceCounter = 0;

    /**
     * Perform one transformation utility condition against multiple files,
     * returning true if at least one file meets the condition (default mode).
     * There is an alternative mode where all files need to meet the
     * evaluation condition to result in true. For conditions
     * based on comparing two files see {@link DoubleCondition}.
     * For conditions based on evaluating a single file see {@link MultipleConditions}
     *
     * @param conditionTemplate the utility condition template used to create conditions,
     *                          used to be evaluated against the list of files
     */
    public MultipleConditions(SingleCondition conditionTemplate) {
        setConditionTemplate(conditionTemplate);
    }

    /**
     * Set the evaluation mode. The default mode is "at least one",
     * which means the result will be true if at least one file
     * meets the condition. The alternative mode is "all", which
     * requires all files to meet the evaluation condition to result
     * in true.
     *
     * @param mode the evaluation mode
     * @return this utility condition instance
     */
    public MultipleConditions setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    /**
     * Sets one or more transformation context attributes that hold list of Files
     * which the condition should perform against.
     * If more than one attribute is specified, all list of files will be
     * combined into a single one.<br>
     *
     * @param filesAttributes one or more transformation context attributes that hold list
     *                   of Files which the condition should perform
     *                   against
     * @return this transformation utility object
     */
    public MultipleConditions setFiles(String... filesAttributes) {
        this.filesAttributes = filesAttributes;
        return this;
    }

    /**
     * Set the utility condition template used to create conditions
     * to be evaluated against the list of files
     *
     * @param conditionTemplate the utility condition template used to create conditions
     *                          to be evaluated against the list of files
     * @return this utility condition instance
     */
    public MultipleConditions setConditionTemplate(SingleCondition conditionTemplate) {
        checkForNull("conditionTemplate", conditionTemplate);
        this.conditionTemplate = conditionTemplate;
        return this;
    }

    @Override
    public MultipleConditions setName(String name) {
        conditionTemplate.setName(String.format("%s-%s-TEMPLATE_CONDITION", name, conditionTemplate.getClass().getSimpleName()));
        return super.setName(name);
    }

    /**
     * Return the evaluation mode
     *
     * @return the evaluation mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * A copy of the array of transformation context attributes that hold list of Files
     * which the condition should perform against.
     *
     * @return a copy of the array of transformation context attributes that hold list of Files
     * which the condition should perform against
     */
    public String[] getFilesAttributes() {
        return Arrays.copyOf(filesAttributes, filesAttributes.length);
    }

    /**
     * Return the condition template
     *
     * @return the condition template
     */
    public SingleCondition getConditionTemplate() {
        return conditionTemplate;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, conditionTemplate.getName());
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

        if (allFiles.size() == 0) {
            TransformationUtilityException e = new TransformationUtilityException("No pom files have been specified");
            return TUExecutionResult.error(this, e);
        }

        String details = null;
        if(logger.isDebugEnabled()) {
            details = String.format("Multiple condition %s resulted in a maximum of %d evaluations based on %s", getName(), allFiles.size(), conditionTemplate.getClass().getSimpleName());
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
     * @return the new utility condition created based on this instance
     */
    public UtilityCondition newConditionInstance(File transformedAppFolder, File file) {
        UtilityCondition condition = (UtilityCondition) conditionTemplate.copy();
        condition.relative(TransformationUtility.getRelativePath(transformedAppFolder, file));
        condition.setSaveResult(false);

        conditionInstanceCounter++;
        condition.setName(String.format("%s-%d", conditionTemplate.getName(), conditionInstanceCounter));

        return condition;
    }

}
