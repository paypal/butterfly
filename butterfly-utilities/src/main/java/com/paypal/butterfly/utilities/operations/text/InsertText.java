package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.utilities.operations.EolBufferedReader;
import com.paypal.butterfly.utilities.operations.EolHelper;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static com.paypal.butterfly.utilities.operations.EolHelper.removeEol;

/**
 * Inserts text from one file into another text file.
 * The text can be inserted:
 * <ol>
 *     <li>InsertionMode.CONCAT: At the final of the file (default)</li>
 *     <li>InsertionMode.LINE_NUMBER: At one particular specified line number (first line is number 1)</li>
 *     <li>InsertionMode.REGEX_FIRST: Right after only the first line to match the specified regular expression</li>
 *     <li>InsertionMode.REGEX_ALL: Right after any line to match the specified regular expression</li>
 * </ol>
 * Notice concat is the default insertion mode. It is also important to state that the text to be inserted will
 * always start on a new line and, if not placed on the end of the file, the continuation of the original text
 * will follow in a new line, even if the inserted text does not end with a line break.
 * <br>
 * See {@link #setInsertionMode(InsertionMode)}
 *
 * @see InsertionMode
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
     * <br>
     * See {@link #setInsertionMode(InsertionMode)}
     *
     * @see InsertionMode
     */
    public InsertText() {
    }

    /**
     * Operation to insert text into another text file.
     * The text will be inserted at the end of the file,
     * unless another insertion method is specified
     * <br>
     * See {@link #setInsertionMode(InsertionMode)}
     *
     * @param textFileUrl the URL to the text to be inserted
     */
    public InsertText(URL textFileUrl) {
        setTextFileUrl(textFileUrl);
    }

    /**
     * Operation to insert text into another text file.
     * The text will be inserted at the specified line number
     * <br>
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#LINE_NUMBER}
     *
     * @param textFileUrl the URL to the text to be inserted
     * @param lineNumber the line number the text should be added at
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
     * <br>
     * Notice that the insertion mode is automatically set to
     * {@link InsertionMode#REGEX_FIRST}
     *
     * @param textFileUrl the URL to the text to be inserted
     * @param regex the regular expression to find insertion points
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
        checkForNull("InsertionMode", insertionMode);
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
     *
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
     * <br>
     * See {@link #setInsertionMode(InsertionMode)}
     *
     * @see InsertionMode
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

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);

        if (!fileToBeChanged.exists()) {
            // TODO Should this be done as pre-validation?
            FileNotFoundException ex = new FileNotFoundException("File to be modified has not been found");
            return TOExecutionResult.error(this, ex);
        }

        BufferedReader reader = null;
        BufferedReader readerText = null;
        BufferedWriter writer = null;
        TOExecutionResult result = null;

        try {
            final String eol = EolHelper.findEolDefaultToOs(fileToBeChanged);

            File readFile = getOrCreateReadFile(transformedAppFolder, transformationContext);
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(readFile), StandardCharsets.UTF_8));
            readerText = new BufferedReader(new InputStreamReader(textFileUrl.openStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeChanged), StandardCharsets.UTF_8));

            switch (insertionMode) {
                case LINE_NUMBER:
                    result = insertAtSpecificLine(readerText, reader, writer, eol);
                    break;
                case REGEX_FIRST:
                    result = insertAfterRegex(readerText, reader, writer, true, eol);
                    break;
                case REGEX_ALL:
                    result = insertAfterRegex(readerText, reader, writer, false, eol);
                    break;
                default:
                case CONCAT:
                    result = concat(readerText, reader, writer, eol);
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
                } finally {
                    if(readerText != null) try {
                        readerText.close();
                    } catch (IOException e) {
                        result.addWarning(e);
                    }
                }
            }
        }

        return result;
    }

    private TOExecutionResult insertAtSpecificLine(BufferedReader readerText, BufferedReader readerOriginalFile, BufferedWriter writer, String eol) throws IOException {
        String currentLine;
        int n = 1;
        EolBufferedReader eolReaderOriginalFile = new EolBufferedReader(readerOriginalFile);
        boolean textInserted = false;

        for (; n < lineNumber; n++) {
            currentLine = eolReaderOriginalFile.readLineKeepEol();
            if (currentLine == null) {
                break;
            }
            writer.write(currentLine);
        }
        if (n == lineNumber) {
            textInserted = true;
            while((currentLine = readerText.readLine()) != null) {
                writer.write(currentLine);
                writer.write(eol);
            }
        }
        while((currentLine = eolReaderOriginalFile.readLineKeepEol()) != null) {
            writer.write(currentLine);
        }

        if (textInserted) {
            String details = String.format("Text has been inserted from %s to %s at line number %d", textFileUrl, getRelativePath(), lineNumber);
            return TOExecutionResult.success(this, details);
        } else {
            String details = String.format("Text has NOT been inserted at %s because line number %d does not exist", getRelativePath(), lineNumber);
            return TOExecutionResult.noOp(this, details);
        }
    }

    private TOExecutionResult insertAfterRegex(BufferedReader readerText, BufferedReader readerOriginalFile, BufferedWriter writer, boolean firstOnly, String eol) throws IOException {
        String currentLine;
        int n = 0;
        boolean foundFirstMatch = false;
        final Pattern pattern = Pattern.compile(regex);
        EolBufferedReader eolReaderOriginalFile = new EolBufferedReader(readerOriginalFile);
        StringBuilder readerTextStringBuilder = null;
        String readerTextString = null;
        if (!firstOnly) {
            readerTextStringBuilder = new StringBuilder();
        }

        while((currentLine = eolReaderOriginalFile.readLineKeepEol()) != null) {
            writer.write(currentLine);
            if((!firstOnly || !foundFirstMatch) && pattern.matcher(removeEol(currentLine)).matches()) {
                foundFirstMatch = true;
                n++;
                if (n == 1) {
                    while((currentLine = readerText.readLine()) != null) {
                        writer.write(currentLine);
                        writer.write(eol);
                        if (!firstOnly) {
                            readerTextStringBuilder.append(currentLine);
                            readerTextStringBuilder.append(eol);
                        }
                    }
                    if (!firstOnly) {
                        readerTextString = readerTextStringBuilder.toString();
                    }
                } else {
                    writer.write(readerTextString);
                }
            }
        }

        if (foundFirstMatch) {
            String details = String.format("Text has been inserted from %s to %s after %d line(s) that matches regular expression '%s'", textFileUrl, getRelativePath(), n, regex);
            return TOExecutionResult.success(this, details);
        } else {
            String details = String.format("No text has been inserted from %s to %s, since no line has been found to match regular expression '%s'", textFileUrl, getRelativePath(), regex);
            return TOExecutionResult.noOp(this, details);
        }
    }

    private TOExecutionResult concat(BufferedReader readerText, BufferedReader readerOriginalFile, BufferedWriter writer, String eol) throws IOException {
        String currentLine;
        EolBufferedReader eolReaderOriginalFile = new EolBufferedReader(readerOriginalFile);
        boolean lastLineEndsWithEol = true;
        while((currentLine = eolReaderOriginalFile.readLineKeepEol()) != null) {
            writer.write(currentLine);
            lastLineEndsWithEol = EolHelper.endsWithEol(currentLine);
        }
        if (!lastLineEndsWithEol) {
            writer.write(eol);
        }
        while((currentLine = readerText.readLine()) != null) {
            writer.write(currentLine);
            writer.write(eol);
        }

        String details = String.format("Text has been inserted from %s to %s at the end of the file", textFileUrl, getRelativePath());
        return TOExecutionResult.success(this, details);
    }

}
