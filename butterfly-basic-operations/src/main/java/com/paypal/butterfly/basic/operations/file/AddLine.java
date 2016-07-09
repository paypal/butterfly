package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Operation to add a line to a text file
 *
 * @author facarvalho
 */
public class AddLine extends TransformationOperation<AddLine> {

    private static final String DESCRIPTION = "Add line '%s' to file %s.";

    private String newLine;

    /**
     * Operation to add a line to a text file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     */
    private AddLine(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to add a line to a text file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     * @param newLine the new line to be added
     */
    public AddLine(String relativePath, String newLine) {
        this(relativePath);
        this.newLine = newLine;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, newLine, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder) throws Exception {
        // TODO

        return null;
    }

}
