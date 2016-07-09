package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Operation to copy all files recursively from one location to another
 *
 * @author facarvalho
 */
public class CopyFiles extends TransformationOperation<CopyFiles> {

    private static final String DESCRIPTION = "Copy all files recursively from %s to %s.";

    private String newFilesLocation;

    /**
     * Operation to copy all files recursively from one location to another
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     */
    private CopyFiles(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to copy all files recursively from one location to another
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     * @param newFilesLocation location where to copy all files to
     */
    public CopyFiles(String relativePath, String newFilesLocation) {
        this(relativePath);
        this.newFilesLocation = newFilesLocation;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath(), newFilesLocation);
    }

    @Override
    protected String execution(File transformedAppFolder) throws Exception {
        // TODO

        return null;
    }

}