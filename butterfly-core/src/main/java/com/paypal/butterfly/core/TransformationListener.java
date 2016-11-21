package com.paypal.butterfly.core;

import java.util.List;

/**
 * @author facarvalho
 */
public interface TransformationListener {

//    void preTransformation(Transformation transformation, List<TransformationContextImpl> transformationContexts);

    void postTransformation(Transformation transformation, List<TransformationContextImpl> transformationContexts);

//    void preUpgradeStep(Transformation transformation, List<TransformationContextImpl> transformationContexts);

//    void postUpgradeStep(Transformation transformation, List<TransformationContextImpl> transformationContexts);

}
