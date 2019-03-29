package com.paypal.butterfly.core;

import java.io.File;

import org.springframework.stereotype.Component;

import com.paypal.butterfly.extensions.api.exception.ApplicationValidationException;
import com.paypal.butterfly.api.TransformationRequest;

/**
 * Execute checks against application source code before allowing transformation to begin
 *
 * @author facarvalho
 */
@Component
class TransformationValidatorImpl implements TransformationValidator {

    public void preTransformation(TransformationRequest transformationRequest) throws ApplicationValidationException {
        checkPendingManualInstruction(transformationRequest);
    }

    /*
     * Check if this application has pending post-transformation manual instructions to be completed
     */
    private void checkPendingManualInstruction(TransformationRequest transformationRequest) throws ApplicationValidationException {
        File applicationFolder = transformationRequest.getApplication().getFolder();
        File[] matchingFiles = applicationFolder.listFiles(filePath -> filePath.getName().equals(ManualInstructionsHandler.MANUAL_INSTRUCTIONS_MAIN_FILE));
        if (matchingFiles != null && matchingFiles.length > 0) {
            throw new ApplicationValidationException("This application has pending manual instructions. Perform manual instructions at the following file first, then remove it, and run Butterfly again: " + matchingFiles[0].getAbsolutePath());
        }
    }

}
