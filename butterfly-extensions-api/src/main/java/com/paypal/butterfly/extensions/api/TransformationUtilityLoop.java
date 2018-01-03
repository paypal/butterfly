package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allows the execution of any transformation utility instance,
 * including a {@link TransformationUtilityGroup}, multiple times in a loop.
 * The number of iterations is defined by one of these options:
 * <ol>
 *     <li>Specifying the number of iterations.</li>
 *     <li>Specifying a {@link TransformationContext} attribute (by its name) whose value is true or false. If that is not a boolean, or if non-existent, it will be treated as false. If that is false, the loop is interrupted.</li>
 *     <li>Specifying a {@link UtilityCondition} object whose result is true or false. The result of this TU condition object won't be saved to the TC, it will be executed exclusively to the scope of this loop execution. Any result other than a boolean true value, including failures, will be treated as false. If that is false, the loop is interrupted.</li>
 * </ol>
 *
 * @author facarvalho
 */
public class TransformationUtilityLoop extends TransformationUtility<TransformationUtilityLoop> implements TransformationUtilityParent {

    private static final String DESCRIPTION = "Transformation template loop, executing %s";
    private static final String TEMPLATE_NAME_FORMAT = "%s_%s_template";
    private static final String CONDITION_NAME_FORMAT = "%s_%s_condition";

    // Possible ways to define the condition
    private int iterations = -1;
    private String attribute;
    private UtilityCondition condition;

    // The next iteration to be executed, in case a number of iterations has been specified
    private int nextIteration = 1;

    // The TU used as a template to create the actual TU instance to be executed each iteration
    private TransformationUtility template;

    // Because this TU is a parent, it is necessary to be able to return a list of children
    // In this case, the children are the clones created out of the template, which means
    // that the number of children will be the same as the number of executed loop iterations
    private List<TransformationUtility> childrenList = new ArrayList<>();

    /**
     * Allows the execution of any transformation utility instance,
     * including a {@link TransformationUtilityGroup}, multiple times in a loop.
     * The number of iterations is defined by one of these options:
     * <ol>
     *     <li>Specifying the number of iterations.</li>
     *     <li>Specifying a {@link TransformationContext} attribute (by its name) whose value is true or false. If that is not a boolean, or if non-existent, it will be treated as false. If that is false, the loop is interrupted.</li>
     *     <li>Specifying a {@link UtilityCondition} object whose result is true or false. The result of this TU condition object won't be saved to the TC, it will be executed exclusively to the scope of this loop execution. Any result other than a boolean true value, including failures, will be treated as false. If that is false, the loop is interrupted.</li>
     * </ol>
     */
    public TransformationUtilityLoop() {
    }

    /**
     * Allows the execution of any transformation utility instance,
     * including a {@link TransformationUtilityGroup}, multiple times in a loop.
     * The number of iterations is defined by one of these options:
     * <ol>
     *     <li>Specifying the number of iterations.</li>
     *     <li>Specifying a {@link TransformationContext} attribute (by its name) whose value is true or false. If that is not a boolean, or if non-existent, it will be treated as false. If that is false, the loop is interrupted.</li>
     *     <li>Specifying a {@link UtilityCondition} object whose result is true or false. The result of this TU condition object won't be saved to the TC, it will be executed exclusively to the scope of this loop execution. Any result other than a boolean true value, including failures, will be treated as false. If that is false, the loop is interrupted.</li>
     * </ol>
     *
     * @param template the transformation utility instance to be used a template.
     *                 A clone utility instance will be created out of the template
     *                 for each iteration. See {@link #clone()} for further information
     *                 about the clone object.
     */
    public TransformationUtilityLoop(TransformationUtility template) {
        setTemplate(template);
    }

    /**
     * Sets the transformation utility instance to be used as a template.
     * A clone utility instance will be created out of the template
     * for each iteration. See {@link #clone()} for further information
     * about the clone object.
     *
     * @param template the transformation utility instance to be used as template.
     * @return this transformation utility instance
     */
    public TransformationUtilityLoop setTemplate(TransformationUtility template) {
        checkForNull("template", template);

        if (template.getParent() != null) {
            String exceptionMessage = String.format("Invalid attempt to add already registered transformation utility %s to transformation utility loop %s", template.getName(), getName());
            throw new  TransformationDefinitionException(exceptionMessage);
        }

        // Why is this check necessary? What if the TU template is not based on a file?
        if (!template.isFileSet()) {
            String exceptionMessage = String.format("Neither absolute, nor relative path, have been set for transformation utility %s", template.getName());
            throw new  TransformationDefinitionException(exceptionMessage);
        }

        // Even though the template have the loop TU set as its parent, the order is set to 0, and it is not added a child of the loop,
        // since it is not in fact executed (only the instances cloned out of the template are)
        template.setParent(this, 0);
        this.template = template;

        return this;
    }

    /**
     * In this case the condition to execute the next iteration is based on
     * a pre-defined number of iterations to be executed. Each execution
     * decrease the remaining number of iterations.
     *
     * @param iterations the total number of iterations to be executed
     * @return this transformation utility instance
     */
    public TransformationUtilityLoop setCondition(int iterations) {
        if (iterations < 2) {
            throw new TransformationDefinitionException("The number of iterations should be equal or greater than 2");
        }
        this.iterations = iterations;
        return this;
    }

    /**
     * In this case the condition to execute the next iteration is based on
     * a {@link TransformationContext} attribute (specified by its name) whose
     * value is true or false. If that is not a boolean, or if non-existent,
     * it will be treated as false. If that is false, the loop is interrupted.
     *
     * @param attribute the name of the transformation context attribute
     *                  holding the boolean to be used as the condition
     *                  to execute the next iteration. If that is false,
     *                  the loop is interrupted.
     * @return this transformation utility instance
     */
    public TransformationUtilityLoop setCondition(String attribute) {
        checkForBlankString("attribute", attribute);
        this.attribute = attribute;
        return this;
    }

    /**
     * In this case the condition to execute the next iteration is based on
     * a {@link UtilityCondition} object whose result is true or false.
     * The result of this TU condition object won't be saved to the TC,
     * it will be executed exclusively to the scope of this loop execution.
     * Any result other than a boolean true value, including failures, will be treated as false.
     * If that is false, the loop is interrupted.
     *
     * @param condition the {@link UtilityCondition} object whose result
     *                  will be used as the condition to execute the next iteration.
     *                  If that is false, the loop is interrupted.
     * @return this transformation utility instance
     */
    public TransformationUtilityLoop setCondition(UtilityCondition condition) {
        checkForNull("condition", condition);
        if (condition.getName() == null && getName() != null) {
            condition.setName(getName() + "_condition");
        }
        this.condition = condition;
        return this;
    }

    @Override
    protected TransformationUtilityLoop setName(String name) {
        super.setName(name);
        if (template != null) template.setName(String.format(TEMPLATE_NAME_FORMAT, getName(), template.getClass().getSimpleName()));
        if (condition != null) condition.setName(String.format(CONDITION_NAME_FORMAT, getName(), condition.getClass().getSimpleName()));
        return this;
    }

    public TransformationUtility getTemplate() {
        return template;
    }

    public int getIterations() {
        return iterations;
    }

    public String getAttribute() {
        return attribute;
    }

    public TransformationUtility getCondition() {
        return condition;
    }

    public int getNextIteration() {
        return nextIteration;
    }

    @Override
    public String getDescription() {
        String executionCondition = "UNDEFINED_CONDITION";

        if (iterations != -1) {
            executionCondition = iterations + " times";
        } else if (attribute != null) {
            executionCondition = "while " + attribute + " is true";
        } else if (condition != null) {
            executionCondition = "while " + condition.getName() + " is true";
        }

        return String.format(DESCRIPTION, executionCondition);
    }

    @Override
    public List<TransformationUtility> getChildren() {
        return Collections.unmodifiableList(childrenList);
    }

    /**
     * Returns, as its value, the condition to keep iterating over this loop
     *
     * @return a value execution result with the condition to keep iterating over this loop
     */
    @Override
    protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        boolean iterateAgain = false;
        if (iterations >= 2) {
            iterateAgain = nextIteration <= iterations;
        } else if (attribute != null) {
            Object attributeValue = transformationContext.get(attribute);
            iterateAgain = attributeValue instanceof Boolean && ((Boolean) attributeValue).booleanValue();
        } else if (condition != null) {
            TUExecutionResult executionResult = null;
            executionResult = (TUExecutionResult) condition.clone().execution(transformedAppFolder, transformationContext);
            if (executionResult.getType().equals(TUExecutionResult.Type.VALUE)) {
                Object executionValue = executionResult.getValue();
                iterateAgain = executionValue instanceof Boolean && ((Boolean) executionValue).booleanValue();
            } else {
                Exception exception = executionResult.getException();
                if (exception == null) {
                    return TUExecutionResult.warning(this, "Condition template has not returned a value", false);
                } else {
                    return TUExecutionResult.warning(this, exception, false);
                }
            }
        } else {
            return TUExecutionResult.warning(this, "No condition has been specified", false);
        }

        return TUExecutionResult.value(this, iterateAgain);
    }

    /**
     * Returns the TU instance to be run in the this iteration.
     * This instance is created based on the template
     *
     * @return the TU instance to be run in this iteration
     */
    public TransformationUtility run() {
        TransformationUtility iterationClone = template.clone();
        iterationClone.setParent(this, nextIteration);
        childrenList.add(iterationClone);

        return iterationClone;
    }

    /**
     * Returns a clone of this transformation utility loop ready for the next iteration
     *
     * @return a clone of this transformation utility loop ready for the next iteration
     */
    public TransformationUtility iterate() {
        nextIteration++;
        return clone();
    }

}
