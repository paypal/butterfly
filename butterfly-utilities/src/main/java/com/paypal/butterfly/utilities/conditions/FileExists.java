package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.SingleCondition;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;

/**
 * Checks if a particular file or folder exists.
 *
 * @author facarvalho
 */
public class FileExists extends SingleCondition<FileExists> {

    private static final String DESCRIPTION = "Check if file or folder '%s' exists";

    public FileExists() {
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        boolean exists = false;

        try {
            File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);
            exists = pomFile.exists();
        } catch (TransformationUtilityException e) {
            return TUExecutionResult.warning(this, e, exists);
        }

        return TUExecutionResult.value(this, exists);
    }

}
