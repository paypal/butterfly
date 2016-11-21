package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Operation to replace text in a text file
 * based on a regular expression.
 *
 * @author facarvalho
 */
public class ReplaceText extends TransformationOperation<ReplaceText> {

    private static final String DESCRIPTION = "Replace text in %s based on regular expression %s";

    private static final boolean FIRST_ONLY_DEFAULT_VALUE = true;

    // The regular expression used to find the line(s) to
    // be replaced
    private String regex;

    // The replacement text
    private String replacement;

    // Should only the first line found to match the regular expression
    // be replaced, or should all the ones that match it be replaced?
    private boolean firstOnly = FIRST_ONLY_DEFAULT_VALUE;

    /**
     * Operation to replace text in a text file
     * based on a regular expression.
     *
     * @author facarvalho
     */
    public ReplaceText() {
    }

    /**
     * Operation to replace text in a text file
     * based on a regular expression.
     *
     * @param regex the regular expression to find replacement points
     *
     * @author facarvalho
     */
    public ReplaceText(String regex) {
        setRegex(regex);
    }

    /**
     * Operation to replace text in a text file
     * based on a regular expression.
     *
     * @param regex the regular expression to find replacement points
     * @param replacement the replacement text
     *
     * @author facarvalho
     */
    public ReplaceText(String regex, String replacement) {
        setRegex(regex);
        setReplacement(replacement);
    }

    /**
     * Sets the regular expression to find replacement points
     *
     * @param regex the regular expression to find replacement points
     * @return this transformation operation instance
     */
    public ReplaceText setRegex(String regex) {
        checkForBlankString("Regex", regex);
        this.regex = regex;
        return this;
    }

    /**
     * Sets the replacement text
     *
     * @param replacement the replacement text
     * @return this transformation operation instance
     */
    public ReplaceText setReplacement(String replacement) {
        checkForNull("Replacement Text", replacement);
        this.replacement = replacement;
        return this;
    }

    /**
     * Sets whether all lines, or only the first, to match the
     * regular expression will be replaced
     *
     * @param firstOnly all lines, or only the first, to match the
     * regular expression will be replaced
     * @return this transformation operation instance
     */
    public ReplaceText setFirstOnly(boolean firstOnly) {
        this.firstOnly = firstOnly;
        return this;
    }

    public String getRegex() {
        return regex;
    }

    public String getReplacement() {
        return replacement;
    }

    public boolean isFirstOnly() {
        return firstOnly;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath(), regex);
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);
        File tempFile = new File(fileToBeChanged.getAbsolutePath() + "_temp_" + System.currentTimeMillis());
        BufferedReader reader = null;
        BufferedWriter writer = null;
        TOExecutionResult result = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeChanged), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));
            result = replace(reader, writer);
        } catch (IOException e) {
            result = TOExecutionResult.error(this,  e);
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

        boolean bDeleted = fileToBeChanged.delete();
        String details;
        if(bDeleted) {
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

    private TOExecutionResult replace(BufferedReader reader, BufferedWriter writer) throws IOException {
        String currentLine;
        int n = 0;
        boolean foundFirstMatch = false;
        final Pattern pattern = Pattern.compile("(.*)" + regex + "(.*)");
        boolean firstLine = true;
        while((currentLine = reader.readLine()) != null) {
            if((!firstOnly || !foundFirstMatch) && pattern.matcher(currentLine).matches()) {
                foundFirstMatch = true;
                n++;
                currentLine = currentLine.replaceAll(regex, replacement);
            }
            if(!firstLine) {
                writer.write(System.lineSeparator());
            }
            writer.write(currentLine);
            firstLine = false;
        }

        String details = String.format("File %s has had %d line(s) where text replacement was applied based on regular expression '%s'", getRelativePath(), n, regex);
        TOExecutionResult result = null;
        if (n > 0) {
            result = TOExecutionResult.success(this, details);
        } else {
            result = TOExecutionResult.noOp(this, details);
        }

        return result;
    }

    @Override
    public ReplaceText clone() throws CloneNotSupportedException{
        ReplaceText clonedReplaceText = (ReplaceText) super.clone();
        return clonedReplaceText;
    }

}
