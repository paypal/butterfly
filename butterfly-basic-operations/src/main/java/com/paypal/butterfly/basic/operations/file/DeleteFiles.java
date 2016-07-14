package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Operation for multiple files deletion
 *
 * @author facarvalho
 */
public class DeleteFiles extends TransformationOperation<DeleteFiles> {

    private static final String DESCRIPTION = "Delete all files named %s under %s, or any other folder under it, at any level";

    private String fileName;

    public DeleteFiles() {
    }

    /**
     * Operation for multiple files deletion
     *
     * @param fileName name of files to be deleted under {@code relativePath}, or
     *                 any other folder under it, at any level
     */
    public DeleteFiles(String fileName) {
        this.fileName = fileName;
    }

    public DeleteFiles setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, fileName, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        // TODO

        // Take a look at this: https://docs.oracle.com/javase/tutorial/essential/io/walk.html

        return null;
    }

}