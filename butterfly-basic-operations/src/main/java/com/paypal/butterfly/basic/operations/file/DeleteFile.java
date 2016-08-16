package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Operation for single file deletion
 * <strong>Important: the term file here refers to both file and folder</strong>
 *
 * @author facarvalho
 */
public class DeleteFile extends TransformationOperation<DeleteFile> {

    private static final String DESCRIPTION = "Delete file %s";

    /**
     * Operation for single file deletion
     * <strong>Important: the term file here refers to both file and folder</strong>
     */
    public DeleteFile() {
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File fileToBeRemoved = getAbsoluteFile(transformedAppFolder, transformationContext);
        if(!fileToBeRemoved.exists()) {
            return String.format("File '%s' has not been removed because it does not exist", getRelativePath());
        }
        FileUtils.forceDelete(fileToBeRemoved);

        return String.format("File '%s' has been removed", getRelativePath());
    }

}