package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Operation for single folder deletion
 *
 * @author facarvalho
 */
public class DeleteFolder extends TransformationOperation<DeleteFolder> {

    private static final String DESCRIPTION = "Delete folder %s";

    /**
     * Operation for single folder deletion
     *
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
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        String resultMessage;

        File fileToBeRemoved = getAbsoluteFile(transformedAppFolder, transformationContext);
        FileUtils.deleteDirectory(fileToBeRemoved);
        resultMessage = "Folder " + getRelativePath() + " has been deleted";

        return resultMessage;
    }

}