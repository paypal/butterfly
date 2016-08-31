package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Operation for single file renaming
 *
 * @author facarvalho
 */
public class RenameFile extends TransformationOperation<RenameFile> {

    private static final String DESCRIPTION = "Rename file %s to %s";

    private String newName;

    public RenameFile() {
    }

    /**
     * Operation for single file renaming
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
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        String resultMessage;

        File fileToBeRenamed = getAbsoluteFile(transformedAppFolder, transformationContext);
        File newNameFile = new File(fileToBeRenamed.getParent(), newName);
        FileUtils.moveFile(fileToBeRenamed, newNameFile);
        resultMessage = "File " + getRelativePath() + " has been renamed to " + newName;

        return resultMessage;
    }

    @Override
    public RenameFile clone() throws CloneNotSupportedException {
        RenameFile clonedRenameFile = (RenameFile) super.clone();
        clonedRenameFile.newName = this.newName;
        return clonedRenameFile;
    }

}