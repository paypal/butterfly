package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Operation to copy a file
 *
 * @author facarvalho
 */
public class CopyFile extends TransformationOperation<CopyFile> {

    private static final String DESCRIPTION = "Copy file %s to %s";

    private String newFileLocation;

    /**
     * Operation to copy a file
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     */
    private CopyFile(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to copy a file
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     * @param newFileLocation location where to copy the file to
     */
    public CopyFile(String relativePath, String newFileLocation) {
        this(relativePath);
        this.newFileLocation = newFileLocation;
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