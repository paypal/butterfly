package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TOExecutionResult;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Copies a file. The relative or absolute file is the
 * "from" file, while the "to" location is specified via {@link #setToRelative(String)}
 * or {@link #setToAbsolute(String)}
 * <br>
 * <strong>Note:</strong> if you want to copy a set of specific files from one
 * location to another, then use a multiple transformation operation
 * (see {@code TransformationTemplate.addMultiple()}) with {@link CopyFile}. Now, if
 * you want to copy a directory and its content from one location to another, then
 * use {@link CopyDirectory} instead.
 *
 * @see MoveFile
 * @see CopyDirectory
 * @see MoveDirectory
 *
 * @author facarvalho
 */
public class CopyFile extends AbstractToOperation<CopyFile> {

    // TODO document in javadoc that by default it overwrites, in case the file
    // already exists, or, make it configurable to overwrite or not

    private static final String DESCRIPTION = "Copy file %s to %s";

    /**
     * Copies a file. The relative or absolute file is the
     * "from" file, while the "to" location is specified via {@link #setToRelative(String)}
     * or {@link #setToAbsolute(String)}
     */
    public CopyFile() {
        super(DESCRIPTION);
    }

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File fileFrom = getAbsoluteFile(transformedAppFolder, transformationContext);
        File fileTo = getFileTo(transformedAppFolder, transformationContext);
        TOExecutionResult result = null;

        try {
            String details = String.format("File '%s' has been copied to '%s'", getRelativePath(), getRelativePath(transformedAppFolder, fileTo));
            FileUtils.copyFileToDirectory(fileFrom, fileTo);
            result = TOExecutionResult.success(this, details);
        } catch (IOException e) {
            result = TOExecutionResult.error(this, e);
        }

        return result;
    }

}