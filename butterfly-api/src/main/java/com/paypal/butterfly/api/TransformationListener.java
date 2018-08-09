package com.paypal.butterfly.api;

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
     * @param transformationRequest an object describing the transformation request
     */
//    void preTransformation(TransformationRequest transformationRequest);

    /**
     * This event notification happens right after a transformation is successfully completed.
     * If the transformation is an upgrade path, then this notification will be sent only
     * once, in the end, after all upgrade steps are completed.
     *
     * @param transformationRequest an object describing the transformation request
     * @param transformationResult an object describing the transformation result
     */
    void postTransformation(TransformationRequest transformationRequest, TransformationResult transformationResult);

    /**
     * This event notification happens only if a transformation is aborted, and it is sent right after it.
     *
     * @param transformationRequest an object describing the transformation request
     * @param transformationResult an object describing the transformation result
     */
    void postTransformationAbort(TransformationRequest transformationRequest, TransformationResult transformationResult);

//    void preUpgradeStep(TransformationRequest transformationRequest, List<TransformationContextImpl> transformationContexts);

//    void postUpgradeStep(TransformationRequest transformationRequest, List<TransformationContextImpl> transformationContexts);

}
