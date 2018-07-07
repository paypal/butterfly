package com.paypal.butterfly.core;

import java.io.File;

import org.springframework.stereotype.Component;

import com.paypal.butterfly.extensions.api.exception.ApplicationValidationException;

@Component
class TransformationValidatorImpl implements TransformationValidator {

    public void preTransformation(Transformation transformation) throws ApplicationValidationException {
        checkPendingManualInstruction(transformation);
    }

    private void checkPendingManualInstruction(Transformation transformation) throws ApplicationValidationException {
        File applicationFolder = transformation.getApplication().getFolder();
        File[] matchingFiles = applicationFolder.listFiles(filePath -> filePath.getName().equals(MdFileManualInstructionsHandler.MANUAL_INSTRUCTIONS_MAIN_FILE));
        if (matchingFiles != null && matchingFiles.length > 0) {
            throw new ApplicationValidationException("This application has pending manual instructions. Perform manual instructions at the following file first, then remove it, and run Butterfly again: " + matchingFiles[0].getAbsolutePath());
        }
    }

}
