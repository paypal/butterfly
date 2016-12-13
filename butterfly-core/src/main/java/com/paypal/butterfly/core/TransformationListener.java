package com.paypal.butterfly.core;

import java.util.List;

/**
 * Transformation listener objects are notified of transformation events
 *
 * @author facarvalho
 */
public interface TransformationListener {

    /**
     * This event notification happens right before a transformation begins.
     * If the transformation is an upgrade path, then this notification will be sent only
     * once, in the very beginning, before the first upgrade step.
     *
     * @param transformation an object describing the transformation request
     */
//    void preTransformation(Transformation transformation);

    /**
     * This event notification happens right after a transformation is successfully completed.
     * If the transformation is an upgrade path, then this notification will be sent only
     * once, in the end, after all upgrade steps are completed.
     *
     * @param transformation an object describing the transformation request
     * @param transformationContexts unmodifiable list of transformation context objects generated through the transformation.
     *                               They are ordered by upgrade steps, if more than one
     */
    void postTransformation(Transformation transformation, List<TransformationContextImpl> transformationContexts);

    /**
     * This event notification happens only if a transformation is aborted, and it is sent right after it.
     *
     * @param transformation an object describing the transformation request
     * @param transformationContexts unmodifiable list of transformation context objects generated through the transformation.
     *                               They are ordered by upgrade steps, if more than one
     */
    void postTransformationAbort(Transformation transformation, List<TransformationContextImpl> transformationContexts);

//    void preUpgradeStep(Transformation transformation, List<TransformationContextImpl> transformationContexts);

//    void postUpgradeStep(Transformation transformation, List<TransformationContextImpl> transformationContexts);

}
