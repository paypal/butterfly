package com.paypal.butterfly.extensions.api;


import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A transformation operation
 * The default value for {@link #relative(String)} is {@code null}, which means
 * it must be set explicitly, unless an absolute path is set via {@link #absolute(String)}
 * or {@link #absolute(String, String)}
 * </br>
 * Every transformation operation subclass must override {@link #clone()} and every operation
 * specific property defined in the operation subclass must be copied from the original
 * object to the clone object. Properties inherited from this class and its super classes
 * MUST NOT be copied from original object to cloned object, since that is all already taken
 * care of properly by the framework. Notice that name, parent and path (absolute and relative)
 * are NECESSARILY NOT assigned to the clone object
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
    // This is the name of a transformation context attribute
    // whose value is a boolean
    private String conditionAttributeName = null;

    public TransformationOperation() {
        // Different than regular Transformation Utilities, the default value here is null, which means
        // it must be set explicitly by the developer, unless an absolute path is set
        relative(null);
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

        if(conditionAttributeName != null && !(Boolean) transformationContext.get(conditionAttributeName)) {
            // TODO the return type should be complext enough to tell the transformation engine that
            // this operation was skipped. After that, this message here should be logged by the engine as DEBUG
            return String.format("*** SKIPPED *** Operation '%s' has been skipped due to failing condition: %s", getName(), conditionAttributeName);
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
        } finally {
            hasBeenPerformed.set(true);
        }

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
     * might be aborted, depending on {@link #abortOnFailure}
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
     * depending on {@link #abortOnFailure}
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
     * <strong>Notice that abortion here means interrupting the transformation.
     * It does not mean rolling back the changes that have might already been done
     * by this transformation operation by the time it failed<strong/>
     *
     * @param abort
     * @return
     */
    public final TO abortOnFailure(boolean abort) {
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
    public final boolean abortOnFailure() {
        return abortOnFailure;
    }

    public final synchronized TO executeIf(String conditionAttributeName) {
        this.conditionAttributeName = conditionAttributeName;
        return (TO) this;
    }

    /**
     * Returns a clone of this transformation operation. Cloning rules:
     * <ol>
     *     <li>Must have an unassigned (null) name, parent and path (absolute and relative)</li>
     *     <li>Every operation specific property defined in the operation implementation class must be copied from the original object to the clone object</li>
     * </ol>
     * Every transformation operation subclass must override {@link #clone()} and every operation
     * specific property defined in the operation subclass must be copied from the original
     * object to the clone object. Properties inherited from this class and its super classes
     * MUST NOT be copied from original object to cloned object, since that is all already taken
     * care of properly by the framework. Notice that name, parent and path (absolute and relative)
     * are NECESSARILY NOT assigned to the clone object
     *
     * @return a clone of this transformation operation
     */
    @Override
    public TransformationOperation<TO> clone() throws CloneNotSupportedException {
        TransformationOperation<TO> clone = (TransformationOperation<TO>) super.clone();

        // Properties we do NOT want to be in the clone (they are being initialized)
        clone.hasBeenPerformed = new AtomicBoolean(false);

        // Properties we want to be in the clone (they are being copied from original object)
        clone.abortOnFailure = this.abortOnFailure;
        clone.conditionAttributeName = this.conditionAttributeName;

        return clone;
    }

}
