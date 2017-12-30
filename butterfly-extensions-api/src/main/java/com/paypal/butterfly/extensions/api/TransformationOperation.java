package com.paypal.butterfly.extensions.api;


import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Special type of {@link TransformationUtility} that applies a modification to the project.
 * <br>
 * Transformation operations are also known by {@code TO}.
 * <br>
 * Differences between a regular transformation utility (TU) and transformation operations (TO):
 * <ul>
 *     <li>TU never modifies application. TO always does.</li>
 *     <li>TU usually returns a value, but not necessarily. TO never does.</li>
 *     <li>TU usually saves its result, but not necessarily. TO always does.</li>
 *     <li>TO allows multiple operations.</li>
 * </ul>
 *<br>
 * The default value for {@link #relative(String)} is {@code null}, which means
 * it must be set explicitly, unless an absolute path is set via {@link #absolute(String)}
 * or {@link #absolute(String, String)}
 *
 * @author facarvalho
 */
public abstract class TransformationOperation<T extends TransformationOperation> extends TransformationUtility<T> {

    // An optional temporary read-only copy of the file to be modified
    // This file gets automatically deleted after the transformation operation execution
    private File readFile;

    // A prefix used to name the temporary read-only file
    private static final String READ_FILE_PREFIX = "butterfly_";

    public TransformationOperation() {
        // Different than regular Transformation Utilities, the default value here is null, which means
        // it must be set explicitly by the developer, unless an absolute path is set
        relative(null);
    }

    @Override
    protected final T setSaveResult(boolean saveResult) {
        throw new UnsupportedOperationException("Transformation operations must always save results");
    }

    /**
     * Creates and returns a temporary read-only copy of the file to be modified.
     * <br>
     * The file to be modified by any transformation operation is set via
     * {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)}).
     * Some transformation operations though might need to read the file to be modified
     * as a stream, and modify it by writing to an output stream as that same file
     * is read. Since it is impossible to modify a file at the same time it is being read,
     * this utility method offers an convenient way to create a temporary read-only
     * copy of the file to be modified. This copy should be used to be read, while the original
     * file can be modified.
     * <br>
     * <strong>Important notes:</strong>
     * <ol>
     *     <li>At the first time this method is called, the temporary file will be created and returned. If called again, the same temporary file created at the first time will be returned.</li>
     *     <li>The read-only file will not reflect the changes performed in the original file at any moment, always keeping its original state.</li>
     *     <li>There is no need to delete the temporary file after using it. Butterfly automatically deletes it when the JVM terminates.</li>
     * </ol>
     *
     * @param transformedAppFolder the folder where the transformed application code is
     * @param transformationContext the transformation context object
     * @return a temporary read-only copy of the file to be modified
     * @throws IOException if the temporary file could not be created
     */
    protected final File getOrCreateReadFile(File transformedAppFolder, TransformationContext transformationContext) throws IOException {
        if (readFile == null) {
            File originalFile = getAbsoluteFile(transformedAppFolder, transformationContext);
            readFile = File.createTempFile(READ_FILE_PREFIX, null);
            FileUtils.copyFile(originalFile, readFile);
            readFile.setReadOnly();
        }

        return readFile;
    }

    @Override
    public PerformResult perform(File transformedAppFolder, TransformationContext transformationContext) throws TransformationUtilityException {
        PerformResult performResult = super.perform(transformedAppFolder, transformationContext);
        if (readFile != null) {
            readFile.deleteOnExit();
        }

        return performResult;
    }
}
