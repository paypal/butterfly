package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.utilities.operations.EolBufferedReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static com.paypal.butterfly.utilities.operations.EolHelper.removeEol;

/**
 * Abstract operation to manipulate one, or more, lines from a text file.
 * The line to be manipulated is chosen either based on a regular
 * expression, or by the line number.
 * <br>
 * If the regular expression
 * is set, only the first line found to match it will be manipulated,
 * unless {@link #setFirstOnly(boolean)} is set to false, then
 * all lines that match it will be manipulated.
 * <br>
 * If a regular expression and a line number are both set,
 * the line number will take precedence, and the regular expression
 * will be ignored
 *
 * @author facarvalho
 */
public abstract class AbstractLineOperation<T extends AbstractLineOperation> extends TransformationOperation<T> {

    private static final boolean FIRST_ONLY_DEFAULT_VALUE = true;

    // The regular expression used to find the line(s) to
    // be manipulated (unless the line number is set)
    private String regex;

    // Should only the first line found to match the regular expression
    // be manipulated, or should all the ones that match it be manipulated?
    // This is ignored if the line number is set
    private boolean firstOnly = FIRST_ONLY_DEFAULT_VALUE;

    // The number of the line to be manipulated
    private Integer lineNumber = null;

    // Just a verb in the past tense that represents that sort of
    // change the subclass will perform on the line to be manipulated.
    // This is used when setting operation details message
    protected String manipulationWord = "manipulated";

    /**
     * Operation to manipulate one, or more, lines from a text file.
     * The line to be manipulated is chosen either based on a regular
     * expression, or by the line number.
     * <br>
     * If the regular expression
     * is set, only the first found to match it will be manipulated,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be manipulated.
     * <br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     */
    public AbstractLineOperation() {
    }

    /**
     * Operation to manipulate one, or more, lines from a text file.
     * The line to be manipulated is chosen either based on a regular
     * expression, or by the line number.
     * <br>
     * If the regular expression
     * is set, only the first found to match it will be manipulated,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be manipulated.
     * <br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     *
     * @param regex the regular expression to identify the line(s) to be manipulated
     */
    public AbstractLineOperation(String regex) {
        this(regex, FIRST_ONLY_DEFAULT_VALUE);
    }

    /**
     * Operation to manipulate one, or more, lines from a text file.
     * The line to be manipulated is chosen either based on a regular
     * expression, or by the line number.
     * <br>
     * If the regular expression
     * is set, only the first found to match it will be manipulated,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be manipulated.
     * <br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     *
     * @param regex the regular expression to identify the line(s) to be manipulated
     * @param firstOnly if true, only the first line found (from top down) to match
     *                  the regular expression will be manipulated. If false, all of them
     *                  will
     */
    public AbstractLineOperation(String regex, boolean firstOnly) {
        setRegex(regex);
        setFirstOnly(firstOnly);
    }

    /**
     * Operation to manipulate one line from a text file, based on a
     * line number.
     *
     * @param lineNumber the number of the line to be manipulated
     */
    public AbstractLineOperation(Integer lineNumber) {
        setLineNumber(lineNumber);
    }

    /**
     * Set  the regular expression used to find the line(s) to be manipulated.
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     *
     * @param regex the regular expression used to find the line(s) to be manipulated
     * @return this transformation operation instance
     */
    public T setRegex(String regex) {
        checkForBlankString("Regex", regex);
        this.regex = regex;
        return (T) this;
    }

    /**
     * If the regular expression is set (and a line number is not set),
     * either the first line found to match it will be manipulated, or all
     * that match, depending on this field
     *
     * @param firstOnly if true, only the first line found to match the regular expression
     *                  will be manipulated
     * @return this transformation operation instance
     */
    public T setFirstOnly(boolean firstOnly) {
        this.firstOnly = firstOnly;
        return (T) this;
    }

    /**
     * Sets the number of the line to be manipulated. Line number for first line is 1.
     * If this is set, it will determine the line to be manipulated, and the regular
     * expression will be ignored. If the line number set does not exist in the file,
     * no line will be manipulated.
     *
     * @param lineNumber the number of the line to be manipulated
     * @return this transformation operation instance
     */
    public T setLineNumber(Integer lineNumber) {
        checkForNull("Line Number", lineNumber);
        if(lineNumber <= 0){
            throw new TransformationDefinitionException("Line number cannot be negative or zero");
        }
        this.lineNumber = lineNumber;
        return (T) this;
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
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);

        if (!fileToBeChanged.exists()) {
            // TODO Should this be done as pre-validation?
            String details = String.format("Operation '%s' hasn't transformed the application because file '%s', where the change should happen, does not exist", getName(), getRelativePath(transformedAppFolder, fileToBeChanged));
            return TOExecutionResult.noOp(this, details);
        }

        BufferedReader reader = null;
        BufferedWriter writer = null;
        TOExecutionResult result = null;

        try {
            File readFile = getOrCreateReadFile(transformedAppFolder, transformationContext);
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(readFile), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeChanged), StandardCharsets.UTF_8));

            if (lineNumber != null) {
                result = manipulateBasedOnLineNumber(reader, writer);
            } else {
                result = manipulateBasedOnRegex(reader, writer);
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

    private TOExecutionResult manipulateBasedOnLineNumber(BufferedReader reader, BufferedWriter writer) throws IOException {
        String currentLine;
        int n = 0;
        boolean lineManipulated = false;
        EolBufferedReader eolReader = new EolBufferedReader(reader);
        while((currentLine = eolReader.readLineKeepStartEol()) != null) {
            n++;
            if(n == lineNumber) {
                manipulateLine(currentLine, writer);

                lineManipulated = true;
                continue;
            }
            writer.write(currentLine);
        }

        if (lineManipulated) {
            String details = String.format("File %s has had line number %d %s", getRelativePath(), lineNumber, manipulationWord);
            return TOExecutionResult.success(this, details);
        } else {
            String details = String.format("File %s has had no lines %s, since line %s does not exist", getRelativePath(), manipulationWord, lineNumber);
            return TOExecutionResult.noOp(this, details);
        }
    }

    private TOExecutionResult manipulateBasedOnRegex(BufferedReader reader, BufferedWriter writer) throws IOException {
        String currentLine;
        int n = 0;
        boolean foundFirstMatch = false;
        final Pattern pattern = Pattern.compile(regex);
        boolean firstLine = true;
        EolBufferedReader eolReader = new EolBufferedReader(reader);
        boolean written;
        while((currentLine = eolReader.readLineKeepStartEol()) != null) {
            if((!firstOnly || !foundFirstMatch) && pattern.matcher(removeEol(currentLine)).matches()) {
                written = manipulateLine(currentLine, writer);

                if (written) {
                    firstLine = false;
                }
                foundFirstMatch = true;
                n++;
                continue;
            }
            if(firstLine) {
                currentLine = removeEol(currentLine);
            }
            firstLine = false;
            writer.write(currentLine);
        }

        String details = String.format("File %s has had %d line(s) %s based on regular expression '%s'", getRelativePath(), n, manipulationWord, regex);

        TOExecutionResult result;
        if (n == 0) {
            result = TOExecutionResult.noOp(this, details);
        } else {
            result = TOExecutionResult.success(this, details);
        }

        return result;
    }

    /**
     * To be specialized by subclasses, defining what specific change should be done
     *
     * @param lineToBeManipulated if it has an EOL character, it will be in the beginning, not in the end
     * @param writer used to manipulate the file to be changed
     * @return true only if anything has been written in {@code writer}
     * @throws IOException if an IO operation fails
     */
    protected abstract boolean manipulateLine(String lineToBeManipulated, Writer writer) throws IOException;

}
