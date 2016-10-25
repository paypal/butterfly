package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.utilities.UtilityCondition;

import java.io.File;

/**
 * Transformation operation condition to check if
 * a particular file or folder exists
 *
 * @author facarvalho
 */
public class FileExists extends UtilityCondition<FileExists> {

    private static final String DESCRIPTION = "Check if file or folder '%s' exists";

    public FileExists() {
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        boolean exists = pomFile.exists();
        TUExecutionResult result = TUExecutionResult.value(this, exists);

        return result;
    }

}
