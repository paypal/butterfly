package com.paypal.butterfly.extensions.api;


import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A transformation operation
 *
 * @author facarvalho
 */
public abstract class TransformationOperation<TO> extends TransformationUtility<TO, String> {

    // Indicates whether or not this operation has already been
    // executed. Transformation operations are supposed to
    // be executed ONLY ONCE
    private AtomicBoolean hasBeenPerformed = new AtomicBoolean(false);

    // Abort the whole transformation if this operation fails
    private boolean abortOnFailure = true;

    // Optional condition to let this operation be executed
    private TransformationOperationCondition condition = null;

    public TransformationOperation() {
    }

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
     * Performs the transformation operation against
     * the application to be transformed
     *
     * @param transformedAppFolder
     * @param transformationContext
     *
     * @return a message stating with details the operation that has been performed
     */
    public final synchronized String perform(File transformedAppFolder, TransformationContext transformationContext) throws TransformationOperationException {
        if(hasBeenPerformed.get()) {
            throw new IllegalStateException("This transformation operation has already been performed");
        }

        try {
            applyPropertiesFromContext(transformationContext);
        } catch (TransformationUtilityException e) {
            throw new TransformationOperationException(e.getMessage(), e);
        }

        if(condition != null && !condition.evaluate(transformedAppFolder)) {
            // TODO the return type should be complext enough to tell the transformation engine that
            // this operation was skipped. After that, this message here should be logged by the engine as DEBUG
            return String.format("*** SKIPPED *** Operation '%s' skipped due to failing condition: %s", getName(), condition);
        }
        if(!preExecutionValidation(transformedAppFolder)) {
            throw new TransformationOperationException(getName() + " pre-execution validation has failed");
            // TODO create a meta-data here with reasons for pre-validation failures, it should state the error
        }

        // TODO result should be a composite type, including potential warnings
        String resultMessage;
        try {
            resultMessage = execution(transformedAppFolder, transformationContext);
        } catch(Exception e) {
            throw new TransformationOperationException(getName() + " has failed", e);
        }

        hasBeenPerformed.set(true);

        // TODO post validation handling

        return resultMessage;
    }

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
    protected TO abortTransformationOnFailure(boolean abort) {
        // TODO implement this logic in perform method
        abortOnFailure = abort;
        return (TO) this;
    }

    /**
     * Returns whether this operation aborts the transformation or not in
     * case of an operation failure. Notice that this method does NOT
     * change the state this object in any ways, it is just a getter.
     *
     * @return true only if this operation aborts the transformation or not in
     * case of an operation failure
     */
    public boolean abortTransformationOnFailure() {
        return abortOnFailure;
    }

    public final TO executeIf(TransformationOperationCondition condition) {
        this.condition = condition;
        return (TO) this;
    }

}
