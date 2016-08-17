package com.paypal.butterfly.basic.operations.text;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

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
     * Sets the number of the line to be removed. If this is set,
     * it will determine the line to be removed, and the regular
     * expression will be ignored
     *
     * @param lineNumber the number of the line to be removed
     * @return
     */
    public RemoveLine setLineNumber(Integer lineNumber) {
        // TODO add validation via BeanValidations to assure this is always positive
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
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);

        if (!fileToBeChanged.exists()) {
            // TODO take care of this with a result object
            // Should this be done as pre-validation?
            return String.format("*** SKIPPED *** Operation '%s' has been skipped because file '%s', where the line removal should happen, does not exist", getName(), getRelativePath());
        }

        File tempFile = new File(fileToBeChanged.getAbsolutePath() + "_temp_" + System.currentTimeMillis());
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String result;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeChanged), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));

            if (lineNumber != null) {
                result = removeBasedOnLineNumber(reader, writer);
            } else {
                result = removeBasedOnRegex(reader, writer);
            }

        } finally {
            try {
                if (writer != null) writer.close();
            } finally {
                if (reader != null) reader.close();
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

    private String removeBasedOnLineNumber(BufferedReader reader, BufferedWriter writer) throws Exception {
        String currentLine;
        int n = 0;
        boolean firstLine = true;
        while((currentLine = reader.readLine()) != null) {
            if(!firstLine) {
                writer.write(System.lineSeparator());
            }
            n++;
            if(n == lineNumber) {
                continue;
            }
            writer.write(currentLine);
            firstLine = false;
        }
        return String.format("File %s has had line number %d removed", getRelativePath(), lineNumber);
    }

    private String removeBasedOnRegex(BufferedReader reader, BufferedWriter writer) throws Exception {
        String currentLine;
        int n = 0;
        boolean foundFirstMatch = false;
        final Pattern pattern = Pattern.compile(regex);
        boolean firstLine = true;
        while((currentLine = reader.readLine()) != null) {
            if((!firstOnly || !foundFirstMatch) && pattern.matcher(currentLine).matches()) {
                foundFirstMatch = true;
                n++;
                continue;
            }
            if(!firstLine) {
                writer.write(System.lineSeparator());
            }
            writer.write(currentLine);
            firstLine = false;
        }

        return String.format("File %s has had %d line(s) removed based on regular expression '%s'", getRelativePath(), n, regex);
    }

    @Override
    public RemoveLine clone() throws CloneNotSupportedException {
        RemoveLine clone = (RemoveLine) super.clone();
        clone.regex = this.regex;
        clone.firstOnly = this.firstOnly;
        clone.lineNumber = this.lineNumber;

        return clone;
    }

}
