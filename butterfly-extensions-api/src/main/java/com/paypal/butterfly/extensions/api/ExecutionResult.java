package com.paypal.butterfly.extensions.api;

/**
 * The meta-data object resulted after the {@link TransformationUtility} instance has been executed.
 * This is an abstract type, the concrete classes {@link TUExecutionResult} and {@link TOExecutionResult} should
 * be used directly instead.
 *
 * @see TUExecutionResult
 * @see TOExecutionResult
 *
 * @author facarvalho
 */
public abstract class ExecutionResult<S, R, T> extends Result <S, R, T> {

    ExecutionResult(S source) {
        super(source);
    }

    ExecutionResult(S source, T type) {
        super(source, type);
    }

}
