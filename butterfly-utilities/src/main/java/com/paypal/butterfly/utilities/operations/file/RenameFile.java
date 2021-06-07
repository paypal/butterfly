package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Renames a single file.
 *
 * @author facarvalho
 */
public class RenameFile extends TransformationOperation<RenameFile> {

    private static final String DESCRIPTION = "Rename file %s to %s";

    private String newName;

    /**
     * Renames a single file.
     */
    public RenameFile() {
    }

    /**
     * Renames a single file.
     *
     * @param newName new name for the file
     */
    public RenameFile(String newName) {
        setNewName(newName);
    }

    public RenameFile setNewName(String newName) {
        checkForBlankString("New Name",newName);
        this.newName = newName;
        return this;
    }

    public String getNewName() {
        return newName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath(), newName);
    }

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        TOExecutionResult result = null;

        File fileToBeRenamed = getAbsoluteFile(transformedAppFolder, transformationContext);
        File newNameFile = new File(fileToBeRenamed.getParent(), newName);
        try {
            FileUtils.moveFile(fileToBeRenamed, newNameFile);
            String details = String.format("File '%s' has been renamed to '%s'", getRelativePath(), newName);
            result = TOExecutionResult.success(this, details);
        } catch (Exception e) {
            result = TOExecutionResult.error(this, new TransformationOperationException("File could not be renamed", e));
        }

        return result;
    }

}
