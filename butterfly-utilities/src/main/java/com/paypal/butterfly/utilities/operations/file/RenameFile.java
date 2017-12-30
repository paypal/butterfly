package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.TOExecutionResult;
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
            String details = "File " + getRelativePath() + " has been renamed to " + newName;
            result = TOExecutionResult.success(this, details);
        } catch (IOException e) {
            String details = String.format("File %s could not be renamed to %s", getRelativePath(transformedAppFolder, fileToBeRenamed), newName);
            result = TOExecutionResult.error(this, e, details);
        }

        return result;
    }

}