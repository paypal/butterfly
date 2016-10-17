package com.paypal.butterfly.extensions.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract complex result type
 *
 * @author facarvalho
 */
public abstract class Result<S, RT, T> {

    // The source of this result, which could be for example
    // a transformation utility instance or a transformation
    // operation instance
    private S source;

    // The result type
    private T type;

    // A statement detailing the result, regardless of result type
    private String details;

    // Exception object in case of types such as ERROR
    private Exception exception;

    // Warnings associated with this result.
    // Warnings never imply the result is non-successful, only errors do
    private List<Exception> warnings = new ArrayList<>(3);

    Result(S source) {
        setSource(source);
    }

    Result(S source, T type) {
        setSource(source);
        setType(type);
    }

    private RT setSource(S source) {
        if(source == null) {
            throw new IllegalArgumentException("Result source cannot be null");
        }
        this.source = source;
        return (RT) this;
    }

    protected RT setType(T type) {
        if(type == null) {
            throw new IllegalArgumentException("Result type cannot be null");
        }
        this.type = type;
        return (RT) this;
    }

    public RT setDetails(String details) {
        this.details = details;
        return (RT) this;
    }

    /**
     * Set the exception associated with this result.
     * This exception can only be set if the result type allows it.
     * If that is not the case, an {@link IllegalArgumentException}
     * will be thrown
     *
     * @param exception associated with the execution result
     * @return this object
     */
    protected RT setException(Exception exception) {
        if(exception == null) {
            throw new IllegalArgumentException("Exception object cannot be null");
        }
        if(!isExceptionType()) {
            throw new IllegalArgumentException("Exception cannot be assigned to " + type);
        }
        this.exception = exception;
        return (RT) this;
    }

    /**
     * Add a new warning associated with this result.
     * Warnings never imply the result is non-successful,
     * only errors do
     *
     * @param warning the warning to be added
     * @return this object
     */
    public RT addWarning(Exception warning) {
        warnings.add(warning);
        changeTypeOnWarning();
        return (RT) this;
    }

    /**
     * This method is used to notify subclasses that
     * the result type might have to change due to the
     * addition of a warning.
     * </br>
     * Usually it should change from a successful type to a
     * warning type. In case the result is an error kind of
     * type, then it should remain as is.
     */
    protected abstract void changeTypeOnWarning();

    /**
     * Returns true if this result type is supposed to contain an exception,
     * such ERROR
     *
     * @return true only if this result type is supposed to contain an exception
     */
    protected abstract boolean isExceptionType();

    /**
     * Returns true if this result type falls to the dependency failure criteria,
     * which is stated in {@link TransformationUtility#dependsOn(String...)}
     *
     * @return true only if this result type falls to the dependency failure criteria
     */
    protected abstract boolean dependencyFailureCheck();

    /**
     * Returns the source of this result, which could be for example
     * a transformation utility instance or a transformation
     * operation instance
     *
     * @return the source of this result
     */
    public S getSource() {
        return source;
    }

    /**
     * Returns the result type
     *
     * @return the result type
     */
    public T getType() {
        return type;
    }

    /**
     * Return the result details
     *
     * @return the result details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Return the exception object in case of types such as ERROR
     *
     * @return the exception object in case of types such as ERROR
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Return a list of warnings associated with this result.
     * Warnings never imply the result is non-successful, only errors do
     *
     * @return a list of warnings associated with this result
     */
    public List<Exception> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

}
