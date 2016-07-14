package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Operation to copy a file
 *
 * @author facarvalho
 */
public class CopyFile extends TransformationOperation<CopyFile> {

    private static final String DESCRIPTION = "Copy file %s to %s";

    private String newFileLocation;

    public CopyFile() {
    }

    /**
     * Operation to copy a file
     *
     * @param newFileLocation location where to copy the file to
     */
    public CopyFile(String newFileLocation) {
        this.newFileLocation = newFileLocation;
    }

    public CopyFile setNewFileLocation(String newFileLocation) {
        this.newFileLocation = newFileLocation;
        return this;
    }

    public String getNewFileLocation() {
        return newFileLocation;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath(), newFileLocation);
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        // TODO

        return null;
    }

}