package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.exception.ApplicationValidationException;
import com.paypal.butterfly.api.TransformationRequest;

/**
 * Execute checks against application source code before allowing transformation to begin
 *
 * @author facarvalho
 */
public interface TransformationValidator {

    void preTransformation(TransformationRequest transformationRequest) throws ApplicationValidationException;

}
