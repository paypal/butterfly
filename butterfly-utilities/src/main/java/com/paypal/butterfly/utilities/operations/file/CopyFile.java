package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.operations.AbstractToOperation;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Copies a file. The relative or absolute file is the
 * "from" file, while the "to" location is specified via {@link #setToRelative(String)}
 * or {@link #setToAbsolute(String)}
 * <br>
 * <strong>Notes:</strong>
 * <ol>
 *  <li>If <code>destinationDirectory</code> does not exist, it (and any parent directories) will be created.</li>
 *  <li>If a file <code>source</code> in <code>destinationDirectory</code> exists, it will be overwritten.</li>
 *  <li>If you want to copy a set of specific files from one location to another, use a multiple transformation operation (see {@code TransformationTemplate.addMultiple()}) with {@link CopyFile}.</li>
 *  <li>If you want to copy a directory and its content from one location to another, then use {@link CopyDirectory} instead.</li>
 *  <li>If source file is actually a directory, an operation results in error.</li>
 * </ol>
 *
 * @see MoveFile
 * @see CopyDirectory
 * @see MoveDirectory
 *
 * @author facarvalho
 */
public class CopyFile extends AbstractToOperation<CopyFile> {

    private static final String DESCRIPTION = "Copy file %s to %s";

    /**
     * Copies a file. The relative or absolute file is the
     * "from" file, while the "to" location is specified via {@link #setToRelative(String)}
     * or {@link #setToAbsolute(String)}
     * <br>
     * <strong>Notes:</strong>
     * <ol>
     *  <li>If <code>destinationDirectory</code> does not exist, it (and any parent directories) will be created.</li>
     *  <li>If a file <code>source</code> in <code>destinationDirectory</code> exists, it will be overwritten.</li>
     *  <li>If you want to copy a set of specific files from one location to another, use a multiple transformation operation (see {@code TransformationTemplate.addMultiple()}) with {@link CopyFile}.</li>
     *  <li>If you want to copy a directory and its content from one location to another, then use {@link CopyDirectory} instead.</li>
     * </ol>
     */
    public CopyFile() {
        super(DESCRIPTION);
    }

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        // TODO Validation must be done here!!!

        File fileFrom = getAbsoluteFile(transformedAppFolder, transformationContext);
        File fileTo = getFileTo(transformedAppFolder, transformationContext);
        TOExecutionResult result;

        try {
            if (fileFrom.isDirectory()) {
                IOException ex = new IOException(getRelativePath(transformedAppFolder, fileFrom) + " (Is a directory)");
                result = TOExecutionResult.error(this, new TransformationOperationException("File could not be copied", ex));
            } else {
                String details = String.format("File '%s' has been copied to '%s'", getRelativePath(), getRelativePath(transformedAppFolder, fileTo));
                FileUtils.copyFileToDirectory(fileFrom, fileTo);
                result = TOExecutionResult.success(this, details);
            }
        } catch (IOException e) {
            result = TOExecutionResult.error(this, new TransformationOperationException("File could not be copied", e));
        }

        return result;
    }

}