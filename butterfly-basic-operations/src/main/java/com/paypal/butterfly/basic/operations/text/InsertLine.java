package com.paypal.butterfly.basic.operations.text;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
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
        this.insertionMode = insertionMode;
        return this;
    }

    /**
     * Sets the new line to be inserted
     *
     * @param newLine the new line to be inserted
     * @return this transformation operation instance
     */
    public InsertLine setNewLine(String newLine) {
        this.newLine = newLine;
        return this;
    }

    /**
     * Sets the line number the new line should be added at
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#LINE_NUMBER}
     *
     * @param lineNumber the line number the new line should be added at
     * @return this transformation operation instance
     */
    public InsertLine setLineNumber(Integer lineNumber) {
        // TODO add validation via BeanValidations to assure this is always positive
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
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);

        File tempFile = new File(fileToBeChanged.getAbsolutePath() + "_temp_" + System.currentTimeMillis());
        BufferedReader readerOriginalFile = null;
        BufferedWriter writer = null;
        String result;

        try {
            readerOriginalFile = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeChanged), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));

            switch (insertionMode) {
                case LINE_NUMBER:
                    result = insertAtSpecificLine(readerOriginalFile, writer);
                    break;
                case REGEX_FIRST:
                    result = insertAfterRegex(readerOriginalFile, writer, true);
                    break;
                case REGEX_ALL:
                    result = insertAfterRegex(readerOriginalFile, writer, false);
                    break;
                default:
                case CONCAT:
                    result = concat(readerOriginalFile, writer);
                    break;
            }
        } finally {
            try {
                if (writer != null) { writer.close(); }
            } finally {
                if (readerOriginalFile != null) { readerOriginalFile.close(); }
            }
        }

        if(!tempFile.renameTo(fileToBeChanged)) {
            String exceptionMessage = String.format("Error when renaming temporary file %s to %s", getRelativePath(transformedAppFolder, tempFile), getRelativePath(transformedAppFolder, fileToBeChanged));
            throw new TransformationOperationException(exceptionMessage);
        }

        return result;
    }

    private String insertAtSpecificLine(BufferedReader readerOriginalFile, BufferedWriter writer) throws Exception {
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

    private String insertAfterRegex(BufferedReader readerOriginalFile, BufferedWriter writer, boolean firstOnly) throws Exception {
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

    private String concat(BufferedReader readerOriginalFile, BufferedWriter writer) throws Exception {
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
    public InsertLine clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}
