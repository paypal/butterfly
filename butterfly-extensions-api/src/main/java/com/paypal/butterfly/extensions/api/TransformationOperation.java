package com.paypal.butterfly.extensions.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A transformation operation
 *
 * @author facarvalho
 */
public abstract class TransformationOperation<T> {

    private String relativePath;
    private AtomicBoolean hasBeenPerformed = new AtomicBoolean(false);
    private boolean abortOnFailure = true;

    protected TransformationOperation() {
    }

    /**
     * @see {@link #setRelativePath(String)}
     *
     * @param relativePath
     */
    protected TransformationOperation(String relativePath) {
        setRelativePath(relativePath);
    }

    /**
     * Returns a short one line, but SPECIFIC, description about the transformation
     * operation, including mentioning the files and/or folders
     * to be manipulated. This is supposed to be an one line statement about the
     * specific operation that was executed. This would be used for example in
     * log statements or user interfaces.
     *
     * @return a short one line, but specific, description about the transformation
     * operation
     */
    public abstract String getDescription();

    /**
     * Returns an explanation of the criteria to pass or fail pre-execution validation
     * for this transformation operation.
     * </br>
     * You MUST override this method if the operation has a pre-execution validation check.
     *
     * @return an explanation of the criteria to pass or fail pre-execution validation,
     *  or null, in case this operation has no pre-execution validation
     */
    public String getPreValidationExplanation() {
        return null;
    }

    /**
     * Returns an explanation of the criteria to pass or fail post-execution validation
     * for this transformation operation.
     * </br>
     * You MUST override this method if the operation has a post-execution validation check.
     *
     * @return an explanation of the criteria to pass or fail post-execution validation,
     *  or null, in case this operation has no post-execution validation
     */
    public String getPostValidationExplanation() {
        return null;
    }

    /**
     * Returns the relative path from the application root folder
     * to the file or folder the operation should perform against.
     * The File path separator should be based on java.io.File.separator
     *
     * @param relativePath from the application root folder
     *  to the file or folder the operation shoudl be performed against
     */
    public final T setRelativePath(String relativePath) {
        this.relativePath = relativePath;
        return (T) this;
    }

    protected final String getRelativePath() {
        return relativePath;
    }

    /**
     * Performs the transformation operation against
     * the application to be transformed
     *
     * @param transformedAppFolder
     * @return a message stating with details the operation that has been performed
     */
    public final synchronized String perform(File transformedAppFolder) {
        if(hasBeenPerformed.get()) {
            throw new IllegalStateException("This transformation operation has already been performed");
        }
        if(!preExecutionValidation(transformedAppFolder)) {
            throw new IllegalStateException("This transformation operation pre-execution validation has failed");
            // TODO create a meta-data here with reasons for pre-validation failures, it should state the error
            // Fail the whole transformation or not, depending on {@link #abortTransformationOnFailure}
        }

        String resultMessage = execution(transformedAppFolder);

        hasBeenPerformed.set(true);

        // TODO post validation handling

        return resultMessage;
    }

    /**
     * The implementation of this transformation operation
     *
     * @param transformedAppFolder
     *
     * @return an specific status message about the operation
     * execution result
     */
	 // TODO Should throw a specific checked exception in case of validation or execution errors.
	 // TODO return stype should be something that state a successfull message,
	 // but also warnings, if they happen
    protected abstract String execution(File transformedAppFolder);

    /**
     *
     * @return true if this operation has already been performed
     */
    public final boolean hasBeenPerformed() {
        return hasBeenPerformed.get();
    }

    /**
     * Return true only if this operation's pre-req has been met. If this returns
     * false, the operation will NOT be executed, and the whole transformation
     * might be aborted, depending on {@link #abortTransformationOnFailure}
     *
     * @param transformedAppFolder
     *
     * @return
     */
    protected boolean preExecutionValidation(File transformedAppFolder) {
        // TODO implement this logic in perform method
        // TODO create a meta-data here with reasons for pre-validation failures
        return true;
    }

    /**
     * Return true only if this operation's post-execution check has succeeded.
     * If this returns false, the operation WILL NOT be rolled back, but a
     * warning will be stated, and the whole transformation might be aborted,
     * depending on {@link #abortTransformationOnFailure}
     *
     * @param transformedAppFolder
     *
     * @return
     */
    protected boolean postExecutionValidation(File transformedAppFolder) {
        // TODO implement this logic in perform method
        // TODO create a meta-data here with reasons for post-validation failures
        return true;
    }

    /**
     * If set to true, abort the whole transformation if validation or execution fails.
     * If not, just state a warning, aborts the operation execution only.
     *
     * @param abort
     * @return
     */
    protected T abortTransformationOnFailure(boolean abort) {
        // TODO implement this logic in perform method
        abortOnFailure = abort;
        return (T) this;
    }

    /**
     * Returns whether this operation aborts the transformation or not in
     * case of an operation failure
     *
     * @return true only if this operation aborts the transformation or not in
     * case of an operation failure
     */
    public boolean getAbortTransformationOnFailure() {
        return abortOnFailure;
    }

}
