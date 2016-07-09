package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Operation for single file deletion
 *
 * @author facarvalho
 */
public class DeleteFile extends TransformationOperation<DeleteFile> {

    private static final String DESCRIPTION = "Delete file %s.";

    /**
     * Operation for single file deletion
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     */
    public DeleteFile(String relativePath) {
        super(relativePath);
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder) throws Exception {
        File fileToBeRemoved = getAbsoluteFile(transformedAppFolder);
        FileUtils.deleteQuietly(fileToBeRemoved);

        return "File " + getRelativePath() + " has been removed";
    }

}