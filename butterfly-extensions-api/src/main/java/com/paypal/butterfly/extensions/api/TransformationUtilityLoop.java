package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This utility allows the execution of a TU instance, created from a TU template, multiple time in a loop.
 * The number of iterations is defined by one of these options, and in this order of precedence:
 * <ol>
 *     <li>Specifying the number of iterations</li>
 *     <li>Specifying a {@link TransformationContext} attribute (by its name) whose value is true or false. If not a boolean, or if non-existent, it will be treated as false</li>
 *     <li>Specifying a {@link TransformationUtility} object whose result is true or false. In this case, the TU condition object won't be saved to the TC, it will be executed exclusively to the scope of this loop execution. Any result other than a boolean true value, including failures, will be treated as false</li>
 * </ol>
 *
 * @author facarvalho
 */
public class TransformationUtilityLoop extends TransformationUtility<TransformationUtilityLoop> implements TransformationUtilityParent {

    private static final String DESCRIPTION = "Transformation template loop, executing %s";

    // Possible ways to define the condition
    private int iterations = -1;
    private String attribute;
    private TransformationUtility condition;

    // The next iteration to be executed, in case a number of iterations has been specified
    private int nextIteration = 1;

    // The TU used as a template to create the actual TU instance to be executed each iteration
    private TransformationUtility template;

    // Because this TU is a parent, it is necessary to be able to return a list of children
    // In this case there will always be only one child, the template
    private List<TransformationUtility> childrenList = new ArrayList<>();

    public TransformationUtilityLoop() {
    }

    public TransformationUtilityLoop(TransformationUtility template) {
        setTemplate(template);
    }

    public TransformationUtilityLoop setTemplate(TransformationUtility template) {
        checkForNull("template", template);

        if (template.getParent() != null) {
            String exceptionMessage = String.format("Invalid attempt to add already registered transformation utility %s to transformation utility loop %s", template.getName(), getName());
            throw new  TransformationDefinitionException(exceptionMessage);
        }
        if (!template.isFileSet()) {
            String exceptionMessage = String.format("Neither absolute, nor relative path, have been set for transformation utility %s", template.getName());
            throw new  TransformationDefinitionException(exceptionMessage);
        }
        template.setParent(this, 1);
        this.template = template;
        childrenList.add(template);

        return this;
    }

    public TransformationUtilityLoop setCondition(int iterations) {
        if (iterations < 2) {
            throw new TransformationDefinitionException("The number of iterations should be equal or greater than 2");
        }
        this.iterations = iterations;
        return this;
    }

    public TransformationUtilityLoop setCondition(String attribute) {
        checkForBlankString("attribute", attribute);
        this.attribute = attribute;
        return this;
    }

    public TransformationUtilityLoop setCondition(TransformationUtility condition) {
        checkForNull("condition", condition);
        if (condition.getName() == null && getName() != null) {
            condition.setName(getName() + "_condition");
        }
        this.condition = condition;
        return this;
    }

    private static final String TEMPLATE_NAME_FORMAT = "%s_%s_template";
    private static final String CONDITION_NAME_FORMAT = "%s_%s_condition";

    @Override
    TransformationUtilityLoop setName(String name) {
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
        String executionCondition = iterations != -1 ? iterations + " times" : (attribute != null ? "while " + attribute + " is true" : (condition != null ? "while " + condition.getName() + " is true" : null));
        return String.format(DESCRIPTION, executionCondition);
    }

    @Override
    public List<TransformationUtility> getChildren() {
        return Collections.unmodifiableList(childrenList);
    }

    /**
     * Returns, as its value, the condition to keep iterating over this loop
     *
     * @return
     */
    @Override
    protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        String warningMessage;
        boolean iterateAgain = false;
        if (iterations >= 2) {
            iterateAgain = nextIteration <= iterations;
        } else if (attribute != null) {
            Object attributeValue = transformationContext.get(attribute);
            iterateAgain = attributeValue instanceof Boolean && ((Boolean) attributeValue).booleanValue();
        } else if (condition != null) {
            TUExecutionResult executionResult = null;
            try {
                executionResult = (TUExecutionResult) condition.clone().execution(transformedAppFolder, transformationContext);
            } catch (CloneNotSupportedException e) {
                TransformationUtilityException tue = new TransformationUtilityException("The condition transformation utility is not cloneable", e);
                return TUExecutionResult.error(this, tue);
            }
            if (executionResult.getType().equals(TUExecutionResult.Type.VALUE)) {
                Object executionValue = executionResult.getValue();
                iterateAgain = executionValue instanceof Boolean && ((Boolean) executionValue).booleanValue();
            } else {
                Exception exception = executionResult.getException();
                if (exception == null) {
                    return TUExecutionResult.warning(this, false, "Condition template has not returned a value");
                } else {
                    return TUExecutionResult.warning(this, false, exception);
                }
            }
        } else {
            return TUExecutionResult.warning(this, false, "No condition has been specified");
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
        try {
            return template.clone();
        } catch (CloneNotSupportedException e) {
            throw new TransformationUtilityException("The template transformation template is not cloneable", e);
        }
    }

    /**
     * Returns a clone of this transformation utility loop ready for the next iteration
     *
     * @return a clone of this transformation utility loop ready for the next iteration
     */
    public TransformationUtility iterate() {
        nextIteration++;
        try {
            return clone();
        } catch (CloneNotSupportedException e) {
            throw new TransformationUtilityException("This transformation utility loop is not cloneable", e);
        }
    }

}
