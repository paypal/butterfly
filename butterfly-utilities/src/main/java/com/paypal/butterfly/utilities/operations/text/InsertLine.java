package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Operation to insert new line(s) into a text file.
 * The new line can be inserted:
 * <ol>
 *     <li>InsertionMode.CONCAT: At the final of the file (default)</li>
 *     <li>InsertionMode.LINE_NUMBER: At one particular specified line number (first line is number 1)</li>
 *     <li>InsertionMode.REGEX_FIRST: Right after only the first line to match the specified regular expression</li>
 *     <li>InsertionMode.REGEX_ALL: Right after any line to match the specified regular expression</li>
 * </ol>
 * @see {@link #setInsertionMode(InsertionMode)}
 * @see {@link InsertionMode}
 * @author facarvalho
 */
public class InsertLine extends TransformationOperation<InsertLine> {

    /**
     * The new line(s) can be inserted:
     * <ol>
     *     <li>InsertionMode.CONCAT: At the final of the file (default)</li>
     *     <li>InsertionMode.LINE_NUMBER: At one particular specified line number (first line is number 1)</li>
     *     <li>InsertionMode.REGEX_FIRST: Right after only the first line to match the specified regular expression</li>
     *     <li>InsertionMode.REGEX_ALL: Right after any line to match the specified regular expression</li>
     * </ol>
     */
    public enum InsertionMode {
        CONCAT, LINE_NUMBER, REGEX_FIRST, REGEX_ALL
    }

    private static final String DESCRIPTION = "Insert new line(s) into %s";

    private InsertionMode insertionMode = InsertionMode.CONCAT;
    private String newLine;
    private Integer lineNumber = null;
    private String regex = null;

    /**
     * Operation to insert new line(s) into a text file.
     * The new line can be inserted:
     * <ol>
     *     <li>InsertionMode.CONCAT: At the final of the file (default)</li>
     *     <li>InsertionMode.LINE_NUMBER: At one particular specified line number (first line is number 1)</li>
     *     <li>InsertionMode.REGEX_FIRST: Right after only the first line to match the specified regular expression</li>
     *     <li>InsertionMode.REGEX_ALL: Right after any line to match the specified regular expression</li>
     * </ol>
     * @see {@link #setInsertionMode(InsertionMode)}
     * @see {@link InsertionMode}
     * @author facarvalho
     */
    public InsertLine() {
    }

    /**
     * Operation to insert a new line into a text file.
     * The new line will be inserted at the end of the file,
     * unless another insertion method is specified
     *
     * @see {@link #setInsertionMode(InsertionMode)}
     */
    public InsertLine(String newLine) {
        setNewLine(newLine);
    }

    /**
     * Operation to insert a new line into a text file.
     * The new line will be inserted at the specified line number
     * </br>
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#LINE_NUMBER}
     */
    public InsertLine(String newLine, Integer lineNumber) {
        setNewLine(newLine);
        setLineNumber(lineNumber);
        setInsertionMode(InsertionMode.LINE_NUMBER);
    }

    /**
     * Operation to insert a new line into a text file.
     * The new line will be inserted right after only the first
     * line to match the specified regular expression
     * </br>
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#REGEX_FIRST}
     */
    public InsertLine(String newLine, String regex) {
        setNewLine(newLine);
        setRegex(regex);
        setInsertionMode(InsertionMode.REGEX_FIRST);
    }

    /**
     * Sets the insertion mode
     *
     * @param insertionMode the insertion mode
     * @return this transformation operation instance
     */
    public InsertLine setInsertionMode(InsertionMode insertionMode) {
        checkForNull("InsertionMode", insertionMode);
        this.insertionMode = insertionMode;
        return this;
    }

    /**
     * Sets the new line to be inserted. To insert a new blank line,
     * just set {@code newLine} to an empty string {@code ""}
     * @param newLine the new line to be inserted
     * @return this transformation operation instance
     */
    public InsertLine setNewLine(String newLine) {
        checkForNull("New Line", newLine);
        this.newLine = newLine;
        return this;
    }

    /**
     * Sets the line number the new line should be added at.
     * Line number for first line is 1.
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#LINE_NUMBER}.
     *
     * @param lineNumber the line number the new line should be added at
     * @return this transformation operation instance
     */
    public InsertLine setLineNumber(Integer lineNumber) {
        checkForNull("Line Number", lineNumber);
        if(lineNumber <= 0){
            throw new TransformationDefinitionException("Line number cannot be negative or zero");
        }
        this.lineNumber = lineNumber;
        setInsertionMode(InsertionMode.LINE_NUMBER);
        return this;
    }

    /**
     * Sets the regular expression to find insertion points
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#REGEX_FIRST}, unless already set
     * to {@link InsertionMode#REGEX_ALL}
     *
     * @see {@link InsertionMode}
     * @see {@link #setInsertionMode(InsertionMode)}
     * @param regex the regular expression to find insertion points
     * @return this transformation operation instance
     */
    public InsertLine setRegex(String regex) {
        checkForBlankString("Regex", regex);
        this.regex = regex;
        if (!insertionMode.equals(InsertionMode.REGEX_ALL)) {
            setInsertionMode(InsertionMode.REGEX_FIRST);
        }
        return this;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);

        File tempFile = new File(fileToBeChanged.getAbsolutePath() + "_temp_" + System.currentTimeMillis());
        BufferedReader readerOriginalFile = null;
        BufferedWriter writer = null;
        String details = null;
        TOExecutionResult result = null;

        try {
            readerOriginalFile = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeChanged), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));

            switch (insertionMode) {
                case LINE_NUMBER:
                    details = insertAtSpecificLine(readerOriginalFile, writer);
                    break;
                case REGEX_FIRST:
                    details = insertAfterRegex(readerOriginalFile, writer, true);
                    break;
                case REGEX_ALL:
                    details = insertAfterRegex(readerOriginalFile, writer, false);
                    break;
                default:
                case CONCAT:
                    details = concat(readerOriginalFile, writer);
                    break;
            }
            result = TOExecutionResult.success(this, details);
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
                if(readerOriginalFile != null) try {
                    readerOriginalFile.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            }
        }

        boolean bDeleted = fileToBeChanged.delete();
        if(bDeleted) {
            if (!tempFile.renameTo(fileToBeChanged)) {
                details = String.format("Error when renaming temporary file %s to %s", getRelativePath(transformedAppFolder, tempFile), getRelativePath(transformedAppFolder, fileToBeChanged));
                TransformationOperationException e = new TransformationOperationException(details);
                result = TOExecutionResult.error(this, e);
            }
        }else {
            details = String.format("Error when deleting %s", getRelativePath(transformedAppFolder, fileToBeChanged));
            TransformationOperationException e = new TransformationOperationException(details);
            result = TOExecutionResult.error(this, e);
        }

        return result;
    }

    private String insertAtSpecificLine(BufferedReader readerOriginalFile, BufferedWriter writer) throws IOException {
        String currentLine;
        int n = 0;
        while((currentLine = readerOriginalFile.readLine()) != null) {
            n++;
            if (n == lineNumber) {
                writer.write(newLine);
                writer.write(System.lineSeparator());
            }
            writer.write(currentLine);
            writer.write(System.lineSeparator());
        }

        return String.format("A new line has been inserted into %s after line number %d", getRelativePath(), lineNumber);
    }

    private String insertAfterRegex(BufferedReader readerOriginalFile, BufferedWriter writer, boolean firstOnly) throws IOException {
        String currentLine;
        int n = 0;
        boolean foundFirstMatch = false;
        final Pattern pattern = Pattern.compile(regex);
        boolean firstLine = true;
        while((currentLine = readerOriginalFile.readLine()) != null) {
            if(!firstLine) {
                writer.write(System.lineSeparator());
            }
            writer.write(currentLine);
            firstLine = false;
            if((!firstOnly || !foundFirstMatch) && pattern.matcher(currentLine).matches()) {
                foundFirstMatch = true;
                n++;
                writer.write(System.lineSeparator());
                writer.write(newLine);
                firstLine = false;
            }
        }

        String result;

        if (foundFirstMatch) {
            result = String.format("New line(s) has been inserted into %s after %d line(s) that matches regular expression '%s'", getRelativePath(), n, regex);
        } else {
            result = String.format("No new line has been inserted into %s, since no line has been found to match regular expression '%s'", getRelativePath(), regex);
        }

        return result;
    }

    private String concat(BufferedReader readerOriginalFile, BufferedWriter writer) throws IOException {
        String currentLine;
        boolean firstLine = true;
        while((currentLine = readerOriginalFile.readLine()) != null) {
            if(!firstLine) {
                writer.write(System.lineSeparator());
            }
            writer.write(currentLine);
            firstLine = false;
        }
        if(!firstLine) {
            writer.write(System.lineSeparator());
        }
        writer.write(newLine);

        return String.format("A new line has been inserted into %s at the end of the file", getRelativePath());
    }

    @Override
    public InsertLine clone() throws CloneNotSupportedException {
        InsertLine clonedInsertLine = (InsertLine) super.clone();
        return clonedInsertLine;
    }

}
