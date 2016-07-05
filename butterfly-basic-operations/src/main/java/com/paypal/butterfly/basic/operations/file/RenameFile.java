package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Operation for single file renaming
 *
 * @author facarvalho
 */
public class RenameFile extends TransformationOperation<RenameFile> {

    private static final String DESCRIPTION = "Rename file %s to %s.";

    private String newName;

    /**
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     */
    private RenameFile(String relativePath) {
        super(relativePath);
    }

    public RenameFile(String relativePath, String newName) {
        this(relativePath);
        this.newName = newName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath(), newName);
    }

    @Override
    protected String execution(File transformedAppFolder) {
        String resultMessage;

        File fileToBeRenamed = getAbsoluteFile(transformedAppFolder);
        File newNameFile = new File(fileToBeRenamed.getParent(), newName);
        try {
            FileUtils.moveFile(fileToBeRenamed, newNameFile);
            resultMessage = "File " + getRelativePath() + " has been renamed to " + newName;
        } catch (IOException e) {
            // TODO
            resultMessage = e.getMessage();
        }

        return resultMessage;
    }

}