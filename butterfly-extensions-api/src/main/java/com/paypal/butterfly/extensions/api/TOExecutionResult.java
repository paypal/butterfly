package com.paypal.butterfly.extensions.api;

/**
 * The meta-data object resulted after the {@link TransformationOperation} instance has been executed.
 *
 * @see TOExecutionResult
 *
 * @author facarvalho
 */
public class TOExecutionResult extends ExecutionResult<TransformationOperation, TOExecutionResult, TOExecutionResult.Type> {

    /**
     * The type of result after the {@link TransformationOperation} instance has been executed.
     */
    public enum Type {

        /**
         * No error happened, but for some reason the TO didn't apply any change. For example, when it was supposed to
         * delete lines in a text file based on a regular expression, but no lines were found to match the regular expression.
         */
        NO_OP,

        /**
         * The TO executed normally and a change was performed.
         */
        SUCCESS,

        /**
         * The TO executed, a complete and valid change was performed, but a "non-fatal" unexpected situation happened.
         */
        WARNING,

        /**
         * The TO failed to execute, no change or an incomplete change was made, and the transformed application might be now in a corrupted state.
         */
        ERROR,
    }

    private TOExecutionResult(TransformationOperation transformationOperation, Type type) {
        super(transformationOperation, type);
    }

    private TOExecutionResult(TransformationOperation transformationOperation, Type type, Exception exception) {
        this(transformationOperation, type);
        setException(exception);
    }

    /**
     * The default result type is {@link Type#SUCCESS}
     */
    private TOExecutionResult(TransformationOperation transformationOperation, String details) {
        this(transformationOperation, Type.SUCCESS);
        setDetails(details);
    }

    /**
     * Creates and returns a new {@link Type#NO_OP} result
     *
     * @param transformationOperation the {@link TransformationOperation} object that produced this result
     * @param details details about the result
     * @return the created result object
     */
    public static TOExecutionResult noOp(TransformationOperation transformationOperation, String details) {
        return new TOExecutionResult(transformationOperation, Type.NO_OP).setDetails(details);
    }

    /**
     * Creates and returns a new {@link Type#SUCCESS} result
     *
     * @param transformationOperation the {@link TransformationOperation} object that produced this result
     * @param details details about the result
     * @return the created result object
     */
    public static TOExecutionResult success(TransformationOperation transformationOperation, String details) {
        return new TOExecutionResult(transformationOperation, details);
    }

    /**
     * Creates and returns a new {@link Type#WARNING} result
     *
     * @param transformationOperation the {@link TransformationOperation} object that produced this result
     * @return the created result object
     */
    public static TOExecutionResult warning(TransformationOperation transformationOperation) {
        return new TOExecutionResult(transformationOperation, Type.WARNING);
    }

    /**
     * Creates and returns a new {@link Type#WARNING} result
     *
     * @param transformationOperation the {@link TransformationOperation} object that produced this result
     * @param details details about the result
     * @return the created result object
     */
    public static TOExecutionResult warning(TransformationOperation transformationOperation, String details) {
        return new TOExecutionResult(transformationOperation, Type.WARNING).setDetails(details);
    }

    /**
     * Creates and returns a new {@link Type#WARNING} result
     *
     * @param transformationOperation the {@link TransformationOperation} object that produced this result
     * @param exception the exception to be registered as warning to the result object
     * @return the created result object
     */
    public static TOExecutionResult warning(TransformationOperation transformationOperation, Exception exception) {
        return new TOExecutionResult(transformationOperation, Type.WARNING).addWarning(exception);
    }

    /**
     * Creates and returns a new {@link Type#WARNING} result
     *
     * @param transformationOperation the {@link TransformationOperation} object that produced this result
     * @param exception the exception to be registered to the result object
     * @param details details about the result
     * @return the created result object
     */
    public static TOExecutionResult warning(TransformationOperation transformationOperation, Exception exception, String details) {
        return new TOExecutionResult(transformationOperation, Type.WARNING, exception).setDetails(details);
    }

    /**
     * Creates and returns a new {@link Type#ERROR} result
     *
     * @param transformationOperation the {@link TransformationOperation} object that produced this result
     * @param exception the exception to be registered to the result object
     * @return the created result object
     */
    public static TOExecutionResult error(TransformationOperation transformationOperation, Exception exception) {
        return new TOExecutionResult(transformationOperation, Type.ERROR, exception);
    }

    /**
     * Creates and returns a new {@link Type#ERROR} result
     *
     * @param transformationOperation the {@link TransformationOperation} object that produced this result
     * @param exception the exception to be registered to the result object
     * @param details details about the result
     * @return the created result object
     */
    public static TOExecutionResult error(TransformationOperation transformationOperation, Exception exception, String details) {
        return new TOExecutionResult(transformationOperation, Type.ERROR, exception).setDetails(details);
    }

    @Override
    protected void changeTypeOnWarning() {
        if(getType().equals(Type.NO_OP) || getType().equals(Type.SUCCESS)) {
            setType(Type.WARNING);
        }
    }

    @Override
    protected boolean isExceptionType() {
        return getType().equals(Type.ERROR) || getType().equals(Type.WARNING);
    }

    @Override
    protected boolean dependencyFailureCheck() {
        return getType().equals(Type.ERROR);
    }

}
