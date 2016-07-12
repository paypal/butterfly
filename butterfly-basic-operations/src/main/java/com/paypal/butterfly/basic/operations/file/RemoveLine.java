package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Operation to remove one, or more, lines from a text file
 *
 * @author facarvalho
 */
public class RemoveLine extends TransformationOperation<RemoveLine> {

    private static final String DESCRIPTION = "Remove line(s) matching '%s' from file %s";

    private String regex;

    /**
     * Operation to remove one, or more, lines from a text file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     */
    private RemoveLine(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to remove one, or more, lines from a text file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     * @param regex the regular expression to identify the line(s) to be removed
     */
    public RemoveLine(String relativePath, String regex) {
        this(relativePath);
        this.regex = regex;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, regex, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        // TODO

        return null;
    }

}
