package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Moves a directory and its content from one location to another.
 * The directory to be moved is specified from relative
 * or absolute location. If the destination directory
 * does not exist, it is created.
 *
 * @see CopyFile
 * @see CopyDirectory
 * @see MoveFile
 *
 * @author facarvalho
 */
public class MoveDirectory extends AbstractToOperation<MoveDirectory> {

    private static final String DESCRIPTION = "Copy a directory and its content from %s to %s";

    /**
     * Moves a directory and its content from one location to another.
     * The directory to be moved is specified from relative
     * or absolute location. If the destination directory
     * does not exist, it is created.
     */
    public MoveDirectory() {
        super(DESCRIPTION);
    }

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        // TODO Validation must be done here!!! In case none has been set!
        File filesFrom = getAbsoluteFile(transformedAppFolder, transformationContext);
        File fileTo = getFileTo(transformedAppFolder, transformationContext);
        TOExecutionResult result = null;

        try {
            FileUtils.moveDirectory(filesFrom, fileTo);
            String details = String.format("Directory '%s' has been moved to '%s'", getRelativePath(transformedAppFolder, filesFrom), getRelativePath(transformedAppFolder, fileTo));
            result = TOExecutionResult.success(this, details);
        } catch (IOException e) {
            result = TOExecutionResult.error(this, e);
        }

        return result;
    }

}