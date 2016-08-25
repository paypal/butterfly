package com.paypal.butterfly.basic.operations.text;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Operation to insert text into another text file.
 * The text can be inserted:
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
public class InsertText extends TransformationOperation<InsertText> {

    /**
     * The text can be inserted:
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

    private static final String DESCRIPTION = "Insert text from %s to %s";

    private InsertionMode insertionMode = InsertionMode.CONCAT;
    private URL textFileUrl;
    private Integer lineNumber = null;
    private String regex = null;

    /**
     * Operation to insert text into another text file.
     * The text can be inserted:
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
    public InsertText() {
    }

    /**
     * Operation to insert text into another text file.
     * The text will be inserted at the end of the file,
     * unless another insertion method is specified
     *
     * @see {@link #setInsertionMode(InsertionMode)}
     */
    public InsertText(URL textFileUrl) {
        setTextFileUrl(textFileUrl);
    }

    /**
     * Operation to insert text into another text file.
     * The text will be inserted at the specified line number
     * </br>
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#LINE_NUMBER}
     */
    public InsertText(URL textFileUrl, Integer lineNumber) {
        setTextFileUrl(textFileUrl);
        setLineNumber(lineNumber);
        setInsertionMode(InsertionMode.LINE_NUMBER);
    }

    /**
     * Operation to insert text into another text file.
     * The text will be inserted right after only the first
     * line to match the specified regular expression
     * </br>
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#REGEX_FIRST}
     */
    public InsertText(URL textFileUrl, String regex) {
        setTextFileUrl(textFileUrl);
        setRegex(regex);
        setInsertionMode(InsertionMode.REGEX_FIRST);
    }

    /**
     * Sets the insertion mode
     *
     * @param insertionMode the insertion mode
     * @return this transformation operation instance
     */
    public InsertText setInsertionMode(InsertionMode insertionMode) {
        this.insertionMode = insertionMode;
        return this;
    }

    /**
     * Sets the URL to the text to be inserted
     *
     * @param textFileUrl the URL to the text to be inserted
     * @return this transformation operation instance
     */
    public InsertText setTextFileUrl(URL textFileUrl) {
        checkForNull("Text File Url", textFileUrl);
        this.textFileUrl = textFileUrl;
        return this;
    }

    /**
     * Sets the line number the text should be added at.
     * Line number for first line is 1.
     * @param lineNumber the line number the text should be added at
     * @return this transformation operation instance
     */
    public InsertText setLineNumber(Integer lineNumber) {
        checkForNull("Line Number", lineNumber);
        if(lineNumber <= 0){
            throw new TransformationDefinitionException("Line number cannot be negative or zero");
        }
        this.lineNumber = lineNumber;
        return this;
    }

    /**
     * Sets the regular expression to find insertion points
     *
     * @see {@link InsertionMode}
     * @see {@link #setInsertionMode(InsertionMode)}
     * @param regex the regular expression to find insertion points
     * @return this transformation operation instance
     */
    public InsertText setRegex(String regex) {
        checkForBlankString("Regex", regex);
        this.regex = regex;
        return this;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, textFileUrl.getFile(), getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);

        File tempFile = new File(fileToBeChanged.getAbsolutePath() + "_temp_" + System.currentTimeMillis());
        BufferedReader readerOriginalFile = null;
        BufferedReader readerText = null;
        BufferedWriter writer = null;
        String result;

        try {
            readerOriginalFile = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeChanged), StandardCharsets.UTF_8));
            readerText = new BufferedReader(new InputStreamReader(textFileUrl.openStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));

            switch (insertionMode) {
                case LINE_NUMBER:
                    result = insertAtSpecificLine(readerText, readerOriginalFile, writer);
                    break;
                case REGEX_FIRST:
                    result = insertAfterRegex(readerText, readerOriginalFile, writer, true);
                    break;
                case REGEX_ALL:
                    result = insertAfterRegex(readerText, readerOriginalFile, writer, false);
                    break;
                default:
                case CONCAT:
                    result = concat(readerText, readerOriginalFile, writer);
                    break;
            }
        } finally {
            try {
                if (writer != null) { writer.close(); }
            } finally {
                try {
                    if (readerOriginalFile != null) { readerOriginalFile.close(); }
                } finally {
                    if(readerText != null) { readerText.close(); }
                }
            }
        }
        //// TODO: 8/11/2016 : Refactor the delete code after introducing working directory
        boolean bDeleted = fileToBeChanged.delete();
        if(bDeleted) {
            if (!tempFile.renameTo(fileToBeChanged)) {
                String exceptionMessage = String.format("Error when renaming temporary file %s to %s", getRelativePath(transformedAppFolder, tempFile), getRelativePath(transformedAppFolder, fileToBeChanged));
                throw new TransformationOperationException(exceptionMessage);
            }
        }else{
            String exceptionMessage = String.format("Error when deleting the directory %s", fileToBeChanged);
            throw new TransformationOperationException(exceptionMessage);
        }

        return result;
    }

    private String insertAtSpecificLine(BufferedReader readerText, BufferedReader readerOriginalFile, BufferedWriter writer) throws Exception {
        String currentLine;
        int n = 0;
        while((currentLine = readerOriginalFile.readLine()) != null) {
            n++;
            writer.write(currentLine);
            writer.write(System.lineSeparator());
            if (n == lineNumber) {
                while((currentLine = readerText.readLine()) != null) {
                    writer.write(currentLine);
                    writer.write(System.lineSeparator());
                }
            }
        }

        return String.format("Text has been inserted from %s to %s at line number %d", textFileUrl, getRelativePath(), lineNumber);
    }

    private String insertAfterRegex(BufferedReader readerText, BufferedReader readerOriginalFile, BufferedWriter writer, boolean firstOnly) throws Exception {
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
                while((currentLine = readerText.readLine()) != null) {
                    writer.write(System.lineSeparator());
                    writer.write(currentLine);
                    firstLine = false;
                }
            }
        }

        String result;

        if (foundFirstMatch) {
            result = String.format("Text has been inserted from %s to %s after %d line(s) that matches regular expression '%s'", textFileUrl, getRelativePath(), n, regex);
        } else {
            result = String.format("No text has been inserted from %s to %s, since no line has been found to match regular expression '%s'", textFileUrl, getRelativePath(), regex);
        }

        return result;
    }

    private String concat(BufferedReader readerText, BufferedReader readerOriginalFile, BufferedWriter writer) throws Exception {
        String currentLine;
        boolean firstLine = true;
        while((currentLine = readerOriginalFile.readLine()) != null) {
            if(!firstLine) {
                writer.write(System.lineSeparator());
            }
            writer.write(currentLine);
            firstLine = false;
        }
        while((currentLine = readerText.readLine()) != null) {
            if(!firstLine) {
                writer.write(System.lineSeparator());
            }
            writer.write(currentLine);
            firstLine = false;
        }

        return String.format("Text has been inserted from %s to %s at the end of the file", textFileUrl, getRelativePath());
    }

    @Override
    public InsertText clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}
