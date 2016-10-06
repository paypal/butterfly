package com.paypal.butterfly.extensions.api;


import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A transformation operation
 *</br>
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
 * </br>
 * Differences between TU and TO:
 *  - TU never modifies application
 *  - TU usually returns a value
 *  - TU instance can be executed multiple times
 *  - TO always modifies application
 *  - TO never returns a value
 *  - TO instance must executed only once
 *
 * @author facarvalho
 */
public abstract class TransformationOperation<TO> extends TransformationUtility<TO> {

    // Indicates whether or not this operation has already been
    // executed. Transformation operations are supposed to
    // be executed ONLY ONCE
    private AtomicBoolean hasBeenPerformed = new AtomicBoolean(false);

    public TransformationOperation() {
        // Different than regular Transformation Utilities, the default value here is null, which means
        // it must be set explicitly by the developer, unless an absolute path is set
        relative(null);
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
    @Override
    public final synchronized PerformResult perform(File transformedAppFolder, TransformationContext transformationContext) throws TransformationOperationException {
        if(hasBeenPerformed.get()) {
            throw new IllegalStateException("This transformation operation has already been performed");
        }

        PerformResult result = null;

        // Finally executing the operation
        try {
            result = super.perform(transformedAppFolder, transformationContext);
        } catch(Exception e) {
            hasBeenPerformed.set(true);
            throw new TransformationOperationException(getName() + " has failed", e);
        } finally {
            hasBeenPerformed.set(true);
        }

        return result;
    }

    /**
     *
     * @return true if this operation has already been performed
     */
    public final boolean hasBeenPerformed() {
        return hasBeenPerformed.get();
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
        // none

        return clone;
    }

    @Override
    protected final TO setSaveResult(boolean saveResult) {
        throw new UnsupportedOperationException("Transformation operations must always save results");
    }

}
