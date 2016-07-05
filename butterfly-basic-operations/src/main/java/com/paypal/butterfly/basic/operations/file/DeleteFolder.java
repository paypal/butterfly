package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Operation for single folder deletion
 *
 * @author facarvalho
 */
public class DeleteFolder extends TransformationOperation<DeleteFolder> {

    private static final String DESCRIPTION = "Delete folder %s.";

    /**
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     */
    public DeleteFolder(String relativePath) {
        super(relativePath);
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder) {
        String resultMessage;

        File fileToBeRemoved = getAbsoluteFile(transformedAppFolder);
        try {
            FileUtils.deleteDirectory(fileToBeRemoved);
            resultMessage = "Folder " + getRelativePath() + " has been deleted";
        } catch (IOException e) {
            // TODO
            resultMessage = e.getMessage();
        }

        return resultMessage;
    }

}