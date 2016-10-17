package com.paypal.butterfly.extensions.api;

/**
 * The execution result of a {@link TransformationUtility}
 *
 * @author facarvalho
 */
public class TUExecutionResult extends ExecutionResult<TransformationUtility, TUExecutionResult, TUExecutionResult.Type> {

    public enum Type {
        // No error happened, but for some reason the TU didn't result in any value (for example, when it was supposed to
        // find a specific file based on its name, but none was found)
        NULL,

        // The TU executed normally and it resulted in a value to be shared via the transformation context
        VALUE,

        // The TU executed, a "non-fatal" unexpected situation happened, and it resulted in a still valid value, or null
        // Warning types might have exceptions associated with it or not
        WARNING,

        // The TU failed to execute and resulted in no value, or in an invalid one to be discarded
        ERROR,
    }

    // The value returned by the transformation utility, which can be null
    private Object value = null;

    /**
     * A {@link Type#NULL} type of transformation utility result
     */
    private TUExecutionResult(TransformationUtility transformationUtility) {
        super(transformationUtility, Type.NULL);
    }

    /**
     * A {@link Type#VALUE} type of transformation utility result.
     * The value object must not be null.
     */
    private TUExecutionResult(TransformationUtility transformationUtility, Object value) {
        super(transformationUtility, Type.VALUE);
        setValue(value);
    }

    /**
     * A flexible constructor
     */
    private TUExecutionResult(TransformationUtility transformationUtility, Type type) {
        super(transformationUtility, type);
    }

    /**
     * A {@link Type#ERROR} type of transformation utility result
     */
    private TUExecutionResult(TransformationUtility transformationUtility, Exception exception) {
        super(transformationUtility, Type.ERROR);
        setException(exception);
    }

    public static TUExecutionResult nullResult(TransformationUtility transformationUtility) {
        return new TUExecutionResult(transformationUtility);
    }

    public static TUExecutionResult nullResult(TransformationUtility transformationUtility, String details) {
        return new TUExecutionResult(transformationUtility).setDetails(details);
    }

    public static TUExecutionResult value(TransformationUtility transformationUtility, Object value) {
        return new TUExecutionResult(transformationUtility, value);
    }

    public static TUExecutionResult value(TransformationUtility transformationUtility, Object value, String details) {
        return new TUExecutionResult(transformationUtility, value).setDetails(details);
    }

    public static TUExecutionResult warning(TransformationUtility transformationUtility, Object value) {
        return new TUExecutionResult(transformationUtility, Type.WARNING).setValue(value);
    }

    public static TUExecutionResult warning(TransformationUtility transformationUtility, Object value, String details) {
        return new TUExecutionResult(transformationUtility, Type.WARNING).setValue(value).setDetails(details);
    }

    public static TUExecutionResult warning(TransformationUtility transformationUtility, Object value, Exception exception) {
        return new TUExecutionResult(transformationUtility, Type.WARNING).setValue(value).setException(exception);
    }

    public static TUExecutionResult warning(TransformationUtility transformationUtility, Object value, Exception exception, String details) {
        return new TUExecutionResult(transformationUtility, Type.WARNING).setValue(value).setException(exception).setDetails(details);
    }

    public static TUExecutionResult error(TransformationUtility transformationUtility, Exception exception) {
        return new TUExecutionResult(transformationUtility, exception);
    }

    public static TUExecutionResult error(TransformationUtility transformationUtility, Exception exception, String details) {
        return new TUExecutionResult(transformationUtility, exception).setDetails(details);
    }

    public static TUExecutionResult error(TransformationUtility transformationUtility, Object value, Exception exception) {
        return new TUExecutionResult(transformationUtility, exception).setValue(value);
    }

    public static TUExecutionResult error(TransformationUtility transformationUtility, Object value, Exception exception, String details) {
        return new TUExecutionResult(transformationUtility, exception).setDetails(details).setValue(value);
    }

    private TUExecutionResult setValue(Object value) {
        if(value == null) {
            throw new IllegalArgumentException("Value must not be null");
        }
        this.value = value;
        return this;
    }

    @Override
    protected void changeTypeOnWarning() {
        if(getType().equals(Type.NULL) || getType().equals(Type.VALUE)) {
            setType(Type.WARNING);
        }
    }

    /**
     * Returns the value returned by the transformation utility, which can be null
     *
     * @return the value returned by the transformation utility, which can be null
     */
    public Object getValue() {
        return value;
    }

    @Override
    protected boolean isExceptionType() {
        return getType().equals(Type.ERROR) || getType().equals(Type.WARNING);
    }

    @Override
    protected boolean dependencyFailureCheck() {
        return getType().equals(Type.NULL) || getType().equals(Type.ERROR);
    }

}
