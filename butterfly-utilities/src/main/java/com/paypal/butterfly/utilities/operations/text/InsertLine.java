package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.utilities.operations.EolBufferedReader;
import com.paypal.butterfly.utilities.operations.EolHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static com.paypal.butterfly.utilities.operations.EolHelper.getEndEol;
import static com.paypal.butterfly.utilities.operations.EolHelper.removeEol;

/**
 * Inserts new line(s) into a text file.
 * The new line can be inserted:
 * <ol>
 *     <li>InsertionMode.CONCAT: At the final of the file (default)</li>
 *     <li>InsertionMode.LINE_NUMBER: At one particular specified line number (first line is number 1). If the specified number is greater than the number of lines, then a {@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#NO_OP} will be returned</li>
 *     <li>InsertionMode.REGEX_FIRST: Right after only the first line to match the specified regular expression</li>
 *     <li>InsertionMode.REGEX_ALL: Right after any line to match the specified regular expression</li>
 *     <li>InsertionMode.REGEX_BEFORE_FIRST: Right before only the first line to match the specified regular expression</li>
 *     <li>InsertionMode.REGEX_BEFORE_ALL: Right before any line to match the specified regular expression</li>
 * </ol>
 * See {@link #setInsertionMode(InsertionMode)}.

 * @see InsertionMode
 * @author facarvalho
 */
public class InsertLine extends TransformationOperation<InsertLine> {

    /**
     * The new line(s) can be inserted:
     * <ol>
     *     <li>InsertionMode.CONCAT: At the final of the file (default)</li>
     *     <li>InsertionMode.LINE_NUMBER: At one particular specified line number (first line is number 1). If the specified number is greater than the number of lines, then a {@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#NO_OP} will be returned</li>
     *     <li>InsertionMode.REGEX_FIRST: Right after only the first line to match the specified regular expression</li>
     *     <li>InsertionMode.REGEX_ALL: Right after any line to match the specified regular expression</li>
     *     <li>InsertionMode.REGEX_BEFORE_FIRST: Right before only the first line to match the specified regular expression</li>
     *     <li>InsertionMode.REGEX_BEFORE_ALL: Right before any line to match the specified regular expression</li>
     * </ol>
     */
    public enum InsertionMode {
        CONCAT,
        LINE_NUMBER,
        REGEX_FIRST,
        REGEX_ALL,
        REGEX_BEFORE_FIRST,
        REGEX_BEFORE_ALL
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
     *     <li>InsertionMode.LINE_NUMBER: At one particular specified line number (first line is number 1). If the specified number is greater than the number of lines, then a {@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#NO_OP} will be returned</li>
     *     <li>InsertionMode.REGEX_FIRST: Right after only the first line to match the specified regular expression</li>
     *     <li>InsertionMode.REGEX_ALL: Right after any line to match the specified regular expression</li>
     *     <li>InsertionMode.REGEX_BEFORE_FIRST: Right before only the first line to match the specified regular expression</li>
     *     <li>InsertionMode.REGEX_BEFORE_ALL: Right before any line to match the specified regular expression</li>
     * </ol>
     * See {@link #setInsertionMode(InsertionMode)}.
     *
     * @see InsertionMode
     */
    public InsertLine() {
    }

    /**
     * Operation to insert a new line into a text file.
     * The new line will be inserted at the end of the file,
     * unless another insertion method is specified
     *
     * See {@link #setInsertionMode(InsertionMode)}.
     *
     * @param newLine the new line to be inserted
     */
    public InsertLine(String newLine) {
        setNewLine(newLine);
    }

    /**
     * Operation to insert a new line into a text file.
     * The new line will be inserted at the specified line number
     * <br>
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#LINE_NUMBER}
     *
     * @param newLine the new line to be inserted
     * @param lineNumber the line number where the new line will be inserted
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
     * <br>
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#REGEX_FIRST}
     *
     * @param newLine the new line to be inserted
     * @param regex the regular expression used to determine where
     *              the new line should be inserted
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
     * If the specified number is greater than the number of lines,
     * then a {@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#NO_OP} will be returned.
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
     * to {@link InsertionMode#REGEX_ALL}, {@link InsertionMode#REGEX_BEFORE_FIRST}
     * or {@link InsertionMode#REGEX_BEFORE_ALL}.
     * <br>
     * See {@link #setInsertionMode(InsertionMode)}.
     *
     * @see InsertionMode
     * @param regex the regular expression to find insertion points
     * @return this transformation operation instance
     */
    public InsertLine setRegex(String regex) {
        checkForBlankString("Regex", regex);
        this.regex = regex;
        if (!insertionMode.equals(InsertionMode.REGEX_ALL)
                && !insertionMode.equals(InsertionMode.REGEX_BEFORE_FIRST)
                && !insertionMode.equals(InsertionMode.REGEX_BEFORE_ALL)) {
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

        if (!fileToBeChanged.exists()) {
            // TODO Should this be done as pre-validation?
            FileNotFoundException ex = new FileNotFoundException("File to be modified has not been found");
            return TOExecutionResult.error(this, ex);
        }

        BufferedReader reader = null;
        BufferedWriter writer = null;
        TOExecutionResult result = null;

        try {
            final String eol = EolHelper.findEolDefaultToOs(fileToBeChanged);
            File readFile = getOrCreateReadFile(transformedAppFolder, transformationContext);
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(readFile), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeChanged), StandardCharsets.UTF_8));

            switch (insertionMode) {
                case LINE_NUMBER:
                    result = insertAtSpecificLine(reader, writer, eol);
                    break;
                case REGEX_FIRST:
                    result = insertRegex(reader, writer, true, true, eol);
                    break;
                case REGEX_ALL:
                    result = insertRegex(reader, writer, false, true, eol);
                    break;
                case REGEX_BEFORE_FIRST:
                    result = insertRegex(reader, writer, true, false, eol);
                    break;
                case REGEX_BEFORE_ALL:
                    result = insertRegex(reader, writer, false, false, eol);
                    break;
                default:
                case CONCAT:
                    result = concat(reader, writer, eol);
                    break;
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

        return result;
    }

    private TOExecutionResult insertAtSpecificLine(BufferedReader reader, BufferedWriter writer, String eol) throws IOException {
        String currentLine;
        int n = 0;
        EolBufferedReader eolReader = new EolBufferedReader(reader);
        boolean newLineInserted = false;
        while((currentLine = eolReader.readLineKeepEol()) != null) {
            n++;
            if (n == lineNumber) {
                writer.write(newLine);
                writer.write(eol);
                newLineInserted = true;
            }
            writer.write(currentLine);
        }

        String details;
        if (newLineInserted) {
            details = String.format("A new line has been inserted into %s after line number %d", getRelativePath(), lineNumber);
            return TOExecutionResult.success(this, details);
        } else {
            details = String.format("No line has been inserted into %s because line number does not exist %d", getRelativePath(), lineNumber);
            return TOExecutionResult.noOp(this, details);
        }
    }

    private TOExecutionResult insertRegex(BufferedReader reader, BufferedWriter writer, boolean firstOnly, boolean insertAfter, String eol) throws IOException {
        String currentLine;
        int n = 0;
        boolean foundFirstMatch = false;
        final Pattern pattern = Pattern.compile(regex);
        EolBufferedReader eolReader = new EolBufferedReader(reader);

        while((currentLine = eolReader.readLineKeepEol()) != null) {
            if (insertAfter) {
                writer.write(currentLine);
            }
            if((!firstOnly || !foundFirstMatch) && pattern.matcher(removeEol(currentLine)).matches()) {
                foundFirstMatch = true;
                n++;
                if (insertAfter && getEndEol(currentLine) == null) {
                    writer.write(eol);
                }
                writer.write(newLine);
                writer.write(eol);
            }
            if (!insertAfter) {
                writer.write(currentLine);
            }
        }

        String details;
        if (foundFirstMatch) {
            details = String.format("New line(s) has been inserted into %s %s %d line(s) that matches regular expression '%s'", getRelativePath(), (insertAfter? "after": "before"), n, regex);
            return TOExecutionResult.success(this, details);
        } else {
            details = String.format("No new line has been inserted into %s, since no line has been found to match regular expression '%s'", getRelativePath(), regex);
            return TOExecutionResult.noOp(this, details);
        }
    }

    private TOExecutionResult concat(BufferedReader reader, BufferedWriter writer, String eol) throws IOException {
        String currentLine;
        boolean firstLine = true;
        EolBufferedReader eolReader = new EolBufferedReader(reader);

        while((currentLine = eolReader.readLineKeepStartEol()) != null) {
            writer.write(currentLine);
            firstLine = false;
        }
        if(!firstLine) {
            writer.write(eol);
        }
        writer.write(newLine);

        String details = String.format("A new line has been inserted into %s at the end of the file", getRelativePath());
        return TOExecutionResult.success(this, details);
    }

}
