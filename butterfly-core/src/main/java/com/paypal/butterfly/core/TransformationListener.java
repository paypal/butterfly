package com.paypal.butterfly.core;

/**
 * @author facarvalho
 */
public interface TransformationListener {

//    void preTransformation(Transformation transformation, TransformationContextImpl transformationContext);

    void postTransformation(Transformation transformation, TransformationContextImpl transformationContext);

//    void preUpgradeStep(Transformation transformation, TransformationContextImpl transformationContext);

//    void postUpgradeStep(Transformation transformation, TransformationContextImpl transformationContext);

}
