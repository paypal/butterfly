package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.exception.ApplicationValidationException;

public interface TransformationValidator {

    void preTransformation(Transformation transformation) throws ApplicationValidationException ;

}
