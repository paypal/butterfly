package com.paypal.butterfly.extensions.api;

/**
 * Abstract type for transformation executions.
 * This is supposed to be specialized for TUs and TOs
 *
 * @author facarvalho
 */
public abstract class ExecutionResult<S, RT, T> extends Result <S, RT, T> {

    ExecutionResult(S source) {
        super(source);
    }

    ExecutionResult(S source, T type) {
        super(source, type);
    }

}
