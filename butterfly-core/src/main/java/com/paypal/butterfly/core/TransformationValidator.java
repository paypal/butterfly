package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.exception.ApplicationValidationException;
import com.paypal.butterfly.api.TransformationRequest;

public interface TransformationValidator {

    void preTransformation(TransformationRequest transformationRequest) throws ApplicationValidationException ;

}
