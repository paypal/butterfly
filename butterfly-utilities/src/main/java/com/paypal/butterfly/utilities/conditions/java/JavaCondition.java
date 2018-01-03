package com.paypal.butterfly.utilities.conditions.java;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Abstract class to specify a condition to be
 * evaluated against a Java class, which is
 * represented as a {@link CompilationUnit}.
 *
 * @author facarvalho
 */
public abstract class JavaCondition<T extends JavaCondition> {

    private boolean negate = false;

    /**
     * Evaluates this condition against the specified compilation
     * unit and returns the evaluation result. This implementation
     * must ignore the negate property during this evaluation.
     * The negation behavior will be considered in {@link #evaluate(CompilationUnit)}
     *
     * @param compilationUnit the {@link CompilationUnit} that represents
     *                        the Java class to be evaluated
     * @return the evaluation result ignoring negation
     */
    protected abstract boolean eval(CompilationUnit compilationUnit);

    /**
     * Evaluates this condition against the specified compilation
     * unit and returns the evaluation result, including negating
     * it if {@link #isNegate()} is true.
     *
     * @param compilationUnit the {@link CompilationUnit} that represents
     *                        the Java class to be evaluated
     * @return the evaluation result including negation (if applicable)
     */
    // This method's visibility is intentionally being set to package
    @SuppressWarnings("PMD.DefaultPackage")
    final boolean evaluate(CompilationUnit compilationUnit) {
        boolean evalResult = eval(compilationUnit);
        return negate ? !evalResult : evalResult;
    }

    /**
     * Sets whether the result should be negated, meaning,
     * resulting true whenever its evaluation result would
     * normally results false, and vice-versa. The default
     * value is false.
     *
     * @param negate the result should be negated or not
     * @return this transformation utility condition instance
     */
    public T setNegate(boolean negate) {
        this.negate = negate;
        return (T) this;
    }

    /**
     * Returns whether the evaluation result will be negated
     * or not.
     *
     * @see #setNegate(boolean)
     * @return whether the evaluation result will be negated
     * or not
     */
    public boolean isNegate() {
        return negate;
    }

}
