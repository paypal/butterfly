package com.paypal.butterfly.extensions.api;

/**
 * The "perform" result of a {@link TransformationOperation}
 *
 * @author facarvalho
 */
public class TOPerformResult extends Result<TransformationOperation, TOPerformResult, TOPerformResult.Type> {

    public enum Type {
        // The result type is defined based on the operation execution result type
        EXECUTION_RESULT,

        // The TO has a condition associated with it (executeIf) but that condition resulted in false
        SKIPPED_CONDITION,

        // The TO depends on one or more TOs, and at least one of them didn't result in SUCCESS
        SKIPPED_DEPENDENCY,

        // The TO failed pre-validation
        ERROR_PRE_VALIDATION,

        // The TO failed, but not because of its operation execution itself, but because of an internal reason
        // For example, when a TransformationOperationException is thrown because the absolute file
        // the TO should execute against could not be resolved during transformation time
        ERROR
    }

    // The operation execution result, in case
    // type is EXECUTION_RESULT
    private TOExecutionResult executionResult = null;

    /*
     * This means the operation has been executed,
     * and the result type is defined based on the operation execution result type.
     *
     * @param executionResult the operation execution result
     */
    private TOPerformResult(TransformationOperation transformationOperation, TOExecutionResult executionResult) {
        super(transformationOperation, Type.EXECUTION_RESULT);
        if(executionResult == null) {
            throw new IllegalArgumentException("Operation execution result cannot be blank");
        }
        setDetails(executionResult.getDetails());
        this.executionResult = executionResult;
    }

    private TOPerformResult(TransformationOperation transformationOperation, Type type) {
        super(transformationOperation, type);
    }

    private TOPerformResult(TransformationOperation transformationOperation, Type type, String details) {
        super(transformationOperation, type);
        setDetails(details);
    }

    /**
     * This means the operation has been executed,
     * and the result type is defined based on the operation execution result type.
     *
     * @param executionResult the operation execution result
     */
    public static TOPerformResult executionResult(TransformationOperation transformationOperation, TOExecutionResult executionResult) {
        return new TOPerformResult(transformationOperation, executionResult);
    }

    /**
     * This means the operation has not been executed
     * because its pre-requisite condition is not true
     */
    public static TOPerformResult skippedCondition(TransformationOperation transformationOperation, String details) {
        TOPerformResult result = new TOPerformResult(transformationOperation, Type.SKIPPED_CONDITION, details);

        // TODO should it hold the name of the transformation context attribute separately from the details?
        // Maybe not, because the operation itself has that already

        return result;
    }

    /**
     * This means the operation has not been executed
     * because its dependency has not resulted in {@link TOExecutionResult.Type#ERROR}.
     * That means, all these other types below are acceptable to meet the dependency:
     * <ul>
     *     <li>{@link TOExecutionResult.Type#NO_OP}</li>
     *     <li>{@link TOExecutionResult.Type#SUCCESS}</li>
     *     <li>{@link TOExecutionResult.Type#WARNING}</li>
     * </ul>
     */
    public static TOPerformResult skippedDependency(TransformationOperation transformationOperation, String details) {
        TOPerformResult result = new TOPerformResult(transformationOperation, Type.SKIPPED_DEPENDENCY, details);

        // TODO should it hold the name(s) of the dependent transformation operation(s) separately from the details?
        // Maybe not, because the operation itself has that already

        return result;
    }

    /**
     * This means the operation has not been executed
     * because its pre-validation has failed
     */
    public static TOPerformResult errorPreValidation(TransformationOperation transformationOperation, Exception exception, String details) {
        TOPerformResult result = new TOPerformResult(transformationOperation, Type.ERROR_PRE_VALIDATION, details);
        result.setException(exception);
        return result;
    }

    /**
     * This means the operation has not been executed
     * because its pre-validation has failed
     */
    public static TOPerformResult errorPreValidation(TransformationOperation transformationOperation, Exception exception) {
        TOPerformResult result = new TOPerformResult(transformationOperation, Type.ERROR_PRE_VALIDATION);
        result.setException(exception);
        return result;
    }

    /**
     * This means The TO failed, but not because of its operation execution itself, but because of an internal reason.
     * For example, when a TransformationOperationException is thrown because the absolute file the TO should execute
     * against could not be resolved during transformation time
     */
    public static TOPerformResult error(TransformationOperation transformationOperation, Exception exception, String details) {
        TOPerformResult result = new TOPerformResult(transformationOperation, Type.ERROR, details);
        result.setException(exception);
        return result;
    }

    /**
     * This means The TO failed, but not because of its operation execution itself, but because of an internal reason.
     * For example, when a TransformationOperationException is thrown because the absolute file the TO should execute
     * against could not be resolved during transformation time
     */
    public static TOPerformResult error(TransformationOperation transformationOperation, Exception exception) {
        TOPerformResult result = new TOPerformResult(transformationOperation, Type.ERROR);
        result.setException(exception);
        return result;
    }

    @Override
    protected void changeTypeOnWarning() {
        // Nothing do be done here, since warnings do not apply to this type of result
    }

    public TOExecutionResult getExecutionResult() {
        return executionResult;
    }

    @Override
    protected boolean isExceptionType() {
        switch (getType()) {
            case SKIPPED_CONDITION:
            case SKIPPED_DEPENDENCY:
                return false;
            case ERROR_PRE_VALIDATION:
            case ERROR:
                return true;
            case EXECUTION_RESULT:
            default:
                return executionResult.isExceptionType();
        }
    }

}
