package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
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
        File fileToBeRemoved;
        try {
            fileToBeRemoved = getAbsoluteFile(transformedAppFolder, transformationContext);
        } catch (TransformationUtilityException e) {
            // TODO deal with it properly with result type
            return String.format("No file has been removed because file path has not been resolved");
        }
        if(!fileToBeRemoved.exists()) {
            // TODO deal with it properly with result type
            return String.format("File '%s' was not removed because it does not exist", getRelativePath());
        }
        FileUtils.forceDelete(fileToBeRemoved);

        return String.format("File '%s' has been removed", getRelativePath());
    }

}