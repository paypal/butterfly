package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class DeleteFile extends TransformationOperation<DeleteFile> {

    private static final String DESCRIPTION = "Delete file %s.";

    /**
     * @see {@link #setRelativePath(String)}
     *
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
    protected String execution(File transformedAppFolder) {
        File fileToBeRemoved = new File(transformedAppFolder, getRelativePath());
        FileUtils.deleteQuietly(fileToBeRemoved);

        return "File " + getRelativePath() + " has been removed";
    }

}