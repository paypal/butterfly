package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Operation for multiple files deletion
 *
 * @author facarvalho
 */
public class DeleteFiles extends TransformationOperation<DeleteFiles> {

    private static final String DESCRIPTION = "Delete all files named %s under %s, or any other folder under it, at any level.";

    private String fileName;

    /**
     * Operation for multiplee files deletion
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     */
    private DeleteFiles(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation for multiple files deletion
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     *
     * @param fileName name of files to be deleted under relativePath, or
     *                 any other folder under it, at any level
     */
    public DeleteFiles(String relativePath, String fileName) {
        this(relativePath);
        this.fileName = fileName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, fileName, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder) throws Exception {
        // TODO

        return null;
    }

}