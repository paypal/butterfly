package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Moves a file. The relative or absolute file is the
 * "from" file, while the "to" location is specified via {@link #setToRelative(String)}
 * or {@link #setToAbsolute(String)}
 * <br>
 * <strong>Notes:</strong>
 * <ol>
 *  <li>If <code>destinationDirectory</code> does not exist, it (and any parent directories) will be created.</li>
 *  <li>If a file <code>source</code> in <code>destinationDirectory</code> exists, it will be overwritten.</li>
 *  <li>If you want to move a set of specific files from one location to another, then use a multiple transformation operation (see {@code TransformationTemplate.addMultiple()}) with {@link MoveFile}.</li>
 *  <li>If you want to move a directory and its content from one location to another, then use {@link MoveDirectory} instead.</li>
 *  <li>If source file is actually a directory, an operation results in error.</li>
 * </ol>
 *
 * @see CopyFile
 * @see CopyDirectory
 * @see MoveDirectory
 *
 * @author facarvalho
 */
public class MoveFile extends AbstractToOperation<MoveFile> {

    // TODO document in javadoc that by default it overwrites, in case the file
    // already exists, or, make it configurable to overwrite or not

    private static final String DESCRIPTION = "Move file %s to %s";

    /**
     * Moves a file. The relative or absolute file is the
     * "from" file, while the "to" location is specified via {@link #setToRelative(String)}
     * or {@link #setToAbsolute(String)}
     */
    public MoveFile() {
        super(DESCRIPTION);
    }

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File fileFrom = getAbsoluteFile(transformedAppFolder, transformationContext);
        File fileTo = getFileTo(transformedAppFolder, transformationContext);
        TOExecutionResult result;

        // TODO
        // Check if it is really a file and if it exists!

        try {
            if (fileFrom.isDirectory()) {
                IOException ex = new IOException(getRelativePath(transformedAppFolder, fileFrom) + " (Is a directory)");
                result = TOExecutionResult.error(this, new TransformationOperationException("File could not be moved", ex));
            } else {
                String details = String.format("File '%s' has been moved to '%s'", getRelativePath(), getRelativePath(transformedAppFolder, fileTo));
                FileUtils.copyFileToDirectory(fileFrom, fileTo);
                FileUtils.fileDelete(fileFrom.getAbsolutePath());
                result = TOExecutionResult.success(this, details);
            }
        } catch (IOException e) {
            result = TOExecutionResult.error(this, new TransformationOperationException("File could not be moved", e));
        }

        return result;
    }

}