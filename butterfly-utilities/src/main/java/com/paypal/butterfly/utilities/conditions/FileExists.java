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

    // Even though it is redundant to have this default constructor here, since it is
    // the only one (the compiler would have added it implicitly), this is being explicitly
    // set here to emphasize that the public default constructor should always be
    // available by any transformation utility even when additional constructors are present.
    // The reason for that is the fact that one or more of its properties might be set
    // during transformation time, using the TransformationUtility set method
    @SuppressWarnings("PMD.UnnecessaryConstructor")
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
