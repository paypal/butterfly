package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.operations.EolBufferedReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static com.paypal.butterfly.utilities.operations.EolBufferedReader.*;

/**
 * Operation to remove one, or more, lines from a text file.
 * The line to be removed is chosen either based on a regular
 * expression, or by the line number.
 * </br>
 * If the regular expression
 * is set, only the first line found to match it will be removed,
 * unless {@link #setFirstOnly(boolean)} is set to false, then
 * all lines that match it will be removed.
 * </br>
 * If a regular expression and a line number are both set,
 * the line number will take precedence, and the regular expression
 * will be ignored
 *
 * @author facarvalho
 */
public class RemoveLine extends TransformationOperation<RemoveLine> {

    private static final String DESCRIPTION = "Remove line(s) from file %s";

    private static final boolean FIRST_ONLY_DEFAULT_VALUE = true;

    // The regular expression used to find the line(s) to
    // be removed (unless the line number is set)
    private String regex;

    // Should only the first line found to match the regular expression
    // be removed, or should all the ones that match it be removed?
    // This is ignored if the line number is set
    private boolean firstOnly = FIRST_ONLY_DEFAULT_VALUE;

    // The number of the line to be removed
    private Integer lineNumber = null;

    /**
     * Operation to remove one, or more, lines from a text file.
     * The line to be removed is chosen either based on a regular
     * expression, or by the line number.
     * </br>
     * If the regular expression
     * is set, only the first found to match it will be removed,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be removed.
     * </br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     */
    public RemoveLine() {
    }

    /**
     * Operation to remove one, or more, lines from a text file.
     * The line to be removed is chosen either based on a regular
     * expression, or by the line number.
     * </br>
     * If the regular expression
     * is set, only the first found to match it will be removed,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be removed.
     * </br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     *
     * @param regex the regular expression to identify the line(s) to be removed
     */
    public RemoveLine(String regex) {
        this(regex, FIRST_ONLY_DEFAULT_VALUE);
    }

    /**
     * Operation to remove one, or more, lines from a text file.
     * The line to be removed is chosen either based on a regular
     * expression, or by the line number.
     * </br>
     * If the regular expression
     * is set, only the first found to match it will be removed,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be removed.
     * </br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     *
     * @param regex the regular expression to identify the line(s) to be removed
     * @param firstOnly if true, only the first line found (from top down) to match
     *                  the regular expression will be removed. If false, all of them
     *                  will
     */
    public RemoveLine(String regex, boolean firstOnly) {
        setRegex(regex);
        setFirstOnly(firstOnly);
    }

    /**
     * Operation to remove one line from a text file, based on a
     * line number.
     *
     * @param lineNumber the number of the line to be removed
     */
    public RemoveLine(Integer lineNumber) {
        setLineNumber(lineNumber);
    }

    /**
     * Set  the regular expression used to find the line(s) to be removed.
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     *
     * @param regex the regular expression used to find the line(s) to be removed
     * @return
     */
    public RemoveLine setRegex(String regex) {
        checkForBlankString("Regex", regex);
        this.regex = regex;
        return this;
    }

    /**
     * If the regular expression is set (and a line number is not set),
     * either the first line found to match it will be removed, or all
     * that match, depending on this field
     *
     * @param firstOnly
     * @return
     */
    public RemoveLine setFirstOnly(boolean firstOnly) {
        this.firstOnly = firstOnly;
        return this;
    }

    /**
     * Sets the number of the line to be removed. Line number for first line is 1.
     * If this is set, it will determine the line to be removed, and the regular
     * expression will be ignored. If the line number set does not exist in the file,
     * no line will be removed.
     *
     * @param lineNumber the number of the line to be removed
     * @return
     */
    public RemoveLine setLineNumber(Integer lineNumber) {
        checkForNull("Line Number", lineNumber);
        if(lineNumber <= 0){
            throw new TransformationDefinitionException("Line number cannot be negative or zero");
        }
        this.lineNumber = lineNumber;
        return this;
    }

    public String getRegex() {
        return regex;
    }

    public boolean isFirstOnly() {
        return firstOnly;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);

        String details;

        if (!fileToBeChanged.exists()) {
            // TODO Should this be done as pre-validation?
            details = String.format("Operation '%s' hasn't transformed the application because file '%s', where the line removal should happen, does not exist", getName(), getRelativePath(transformedAppFolder, fileToBeChanged));
            return TOExecutionResult.noOp(this, details);
        }

        File tempFile = new File(fileToBeChanged.getAbsolutePath() + "_temp_" + System.currentTimeMillis());
        BufferedReader reader = null;
        BufferedWriter writer = null;
        TOExecutionResult result = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeChanged), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));

            if (lineNumber != null) {
                result = removeBasedOnLineNumber(reader, writer);
            } else {
                result = removeBasedOnRegex(reader, writer);
            }
        } catch (IOException e) {
            result = TOExecutionResult.error(this, e);
        } finally {
            try {
                if (writer != null) try {
                    writer.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            } finally {
                if(reader != null) try {
                    reader.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            }
        }
        // TODO Refactor the delete code after introducing working directory
        boolean deleted = fileToBeChanged.delete();
        if(deleted) {
            if (!tempFile.renameTo(fileToBeChanged)) {
                details = String.format("Error when renaming temporary file %s to %s", getRelativePath(transformedAppFolder, tempFile), getRelativePath(transformedAppFolder, fileToBeChanged));
                TransformationOperationException e = new TransformationOperationException(details);
                result = TOExecutionResult.error(this, e);
            }
        } else {
            details = String.format("Error when deleting %s", getRelativePath(transformedAppFolder, fileToBeChanged));
            TransformationOperationException e = new TransformationOperationException(details);
            result = TOExecutionResult.error(this, e);
        }

        return result;
    }

    private TOExecutionResult removeBasedOnLineNumber(BufferedReader reader, BufferedWriter writer) throws IOException {
        String currentLine;
        int n = 0;
        boolean lineRemoved = false;
        EolBufferedReader eolReader = new EolBufferedReader(reader);
        while((currentLine = eolReader.readLineKeepStartEOL()) != null) {
            n++;
            if(n == lineNumber) {
                lineRemoved = true;
                continue;
            }
            writer.write(currentLine);
        }

        if (lineRemoved) {
            String details = String.format("File %s has had line number %d removed", getRelativePath(), lineNumber);
            return TOExecutionResult.success(this, details);
        } else {
            String details = String.format("File %s has had no lines removed, since line %s does not exist", getRelativePath(), lineNumber);
            return TOExecutionResult.noOp(this, details);
        }
    }

    private TOExecutionResult removeBasedOnRegex(BufferedReader reader, BufferedWriter writer) throws IOException {
        String currentLine;
        int n = 0;
        boolean foundFirstMatch = false;
        final Pattern pattern = Pattern.compile(regex);
        boolean firstLine = true;
        EolBufferedReader eolReader = new EolBufferedReader(reader);
        while((currentLine = eolReader.readLineKeepStartEOL()) != null) {
            if((!firstOnly || !foundFirstMatch) && pattern.matcher(removeEOL(currentLine)).matches()) {
                foundFirstMatch = true;
                n++;
                continue;
            }
            if(firstLine) {
                currentLine = removeEOL(currentLine);
            }
            writer.write(currentLine);
            firstLine = false;
        }

        String details = String.format("File %s has had %d line(s) removed based on regular expression '%s'", getRelativePath(), n, regex);

        TOExecutionResult result;
        if (n == 0) {
            result = TOExecutionResult.noOp(this, details);
        } else {
            result = TOExecutionResult.success(this, details);
        }

        return result;
    }

    @Override
    public RemoveLine clone() throws CloneNotSupportedException {
        RemoveLine clonedRemoveLine = (RemoveLine) super.clone();
        return clonedRemoveLine;
    }

}
