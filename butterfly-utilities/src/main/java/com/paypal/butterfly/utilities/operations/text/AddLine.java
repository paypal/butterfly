package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.operations.EolHelper;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Adds a new line to the end of a file.
 *
 * @author facarvalho
 */
public class AddLine extends TransformationOperation<AddLine> {

    private static final String DESCRIPTION = "Add line '%s' to file %s";

    private String newLine;

    public AddLine() {
    }

    /**
     * Operation to add a new line to the end of a file. To add a new blank line,
     * just set {@code newLine} to an empty string {@code ""}
     * @param newLine the new line to be added
     */
    public AddLine(String newLine) {
        setNewLine(newLine);
    }

    /**
     * Sets the new line to be inserted. To add a new blank line,
     * just set {@code newLine} to an empty string {@code ""}
     * @param newLine the new line to be inserted
     * @return this transformation operation instance
     */
    public AddLine setNewLine(String newLine) {
        checkForNull("New Line", newLine);
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
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File fileToBeModified = getAbsoluteFile(transformedAppFolder, transformationContext);

        if (!fileToBeModified.exists()) {
            // TODO Should this be done as pre-validation?
            FileNotFoundException ex = new FileNotFoundException("File to be modified has not been found");
            return TOExecutionResult.error(this, ex);
        }

        TOExecutionResult result = null;

        try {
            FileUtils.fileAppend(fileToBeModified.getAbsolutePath(), EolHelper.findEolDefaultToOs(fileToBeModified));
            FileUtils.fileAppend(fileToBeModified.getAbsolutePath(), newLine);
            String details =  "A new line has been added to file " + getRelativePath(transformedAppFolder, fileToBeModified);
            result = TOExecutionResult.success(this, details);
        } catch (IOException e) {
            result = TOExecutionResult.error(this, e);
        }

        return result;
    }

}
