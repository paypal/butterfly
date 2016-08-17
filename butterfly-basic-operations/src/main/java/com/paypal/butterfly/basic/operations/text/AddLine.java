package com.paypal.butterfly.basic.operations.text;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * Operation to add a new line to the end of a file
 *
 * @author facarvalho
 */
public class AddLine extends TransformationOperation<AddLine> {

    private static final String DESCRIPTION = "Add line '%s' to file %s";

    private String newLine;

    public AddLine() {
    }

    /**
     * Operation to add a new line to the end of a file
     *
     * @param newLine the new line to be added
     */
    public AddLine(String newLine) {
        this.newLine = newLine;
    }

    public AddLine setNewLine(String newLine) {
        this.newLine = newLine;
        return this;
    }

    public String getNewLine() {
        return newLine;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, newLine, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File fileToBeModified = getAbsoluteFile(transformedAppFolder, transformationContext);

        FileUtils.fileAppend(fileToBeModified.getAbsolutePath(), System.lineSeparator());
        FileUtils.fileAppend(fileToBeModified.getAbsolutePath(), newLine);

        return "A new line has been added to file " + getRelativePath();
    }

    @Override
    public AddLine clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}
