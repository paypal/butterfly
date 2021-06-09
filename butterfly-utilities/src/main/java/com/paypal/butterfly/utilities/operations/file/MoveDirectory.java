package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.operations.AbstractToOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Moves a directory and its content from one location to another.
 * The directory to be moved is specified from relative
 * or absolute location. The name of the destination
 * directory will not be the same as the original one, but the one
 * specified via {@link #setToRelative(String)}
 * or {@link #setToAbsolute(String)}. If the destination directory
 * already exists, an error is returned.
 *
 * @see CopyFile
 * @see CopyDirectory
 * @see MoveFile
 *
 * @author facarvalho
 */
public class MoveDirectory extends AbstractToOperation<MoveDirectory> {

    private static final String DESCRIPTION = "Move a directory from %s to %s";

    /**
     * Moves a directory and its content from one location to another.
     * The directory to be moved is specified from relative
     * or absolute location. The name of the destination
     * directory will not be the same as the original one, but the one
     * specified via {@link #setToRelative(String)}
     * or {@link #setToAbsolute(String)}. If the destination directory
     * already exists, an error is returned.
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
        } catch (Exception e) {
            result = TOExecutionResult.error(this, new TransformationOperationException("Directory could not be moved", e));
        }

        return result;
    }

}
