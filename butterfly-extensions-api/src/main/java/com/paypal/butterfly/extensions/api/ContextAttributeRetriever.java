package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.metrics.TransformationMetrics;

import java.io.File;

/**
 * Transformation utility to fetch transformation context attributes post
 * transformation time, since they are always set
 * during transformation time.
 * <br>
 * An example of usage of this feature would be implementing
 * {@link TransformationTemplate#getApplicationName()}. The
 * application name mostly will only be known after the transformation
 * has began, but it might be necessary to know it outside of transformation
 * time (after it). For example, the metrics system needs to know it, as
 * seen in {@link TransformationMetrics#getApplicationName()}.
 *
 * @author facarvalho
 */
public class ContextAttributeRetriever<T> extends TransformationUtility {

    private static final String DESCRIPTION = "Retrieves value of transformation context attribute '%s'";

    private String attributeName;
    private T attributeValue;
    private boolean executed = false;

    public ContextAttributeRetriever() {
        setSaveResult(false);
    }

    public ContextAttributeRetriever(String attributeName) {
        setSaveResult(false);
        setAttributeName(attributeName);
    }

    public void setAttributeName(String attributeName) {
        checkForBlankString("attributeName", attributeName);
        this.attributeName = attributeName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, attributeName);
    }

    @Override
    protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        executed = true;
        try {
            attributeValue = (T) transformationContext.get(attributeName);
            return TUExecutionResult.nullResult(this);
        } catch (IllegalArgumentException | ClassCastException ex) {
            return TUExecutionResult.error(this, ex);
        }
    }

    /**
     * Returns the value of the transformation context attribute
     * specified earlier. If the attribute value is null, null is returned.
     * If this method is called prior to the execution of this transformation
     * utility, an {@link IllegalStateException} is thrown.
     *
     * @return the value of the transformation context attribute specified earlier
     */
    public T getAttributeValue() {
        if (!executed) {
            throw new IllegalStateException(getName() + " has not had a chance to be executed yet");
        }
        return attributeValue;
    }

}
