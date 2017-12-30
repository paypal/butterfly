package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.utilities.operations.EolHelper;

import java.io.IOException;
import java.io.Writer;

/**
 * Replaces one, or more, lines from a text file.
 * The line to be replace is chosen either based on a regular
 * expression, or by the line number.
 * <br>
 * If the regular expression
 * is set, only the first line found to match it will be replaced,
 * unless {@link #setFirstOnly(boolean)} is set to false, then
 * all lines that match it will be replaced.
 * <br>
 * If a regular expression and a line number are both set,
 * the line number will take precedence, and the regular expression
 * will be ignored
 *
 * @author facarvalho
 */
public class ReplaceLine extends AbstractLineOperation<ReplaceLine> {

    private static final String DESCRIPTION = "Replace line(s) from file %s";

    private static final String MANIPULATION_WORD = "replaced";

    // The replacement line
    private String replacement;

    /**
     * Operation to replace one, or more, lines from a text file.
     * The line to be replaced is chosen either based on a regular
     * expression, or by the line number.
     * <br>
     * If the regular expression
     * is set, only the first found to match it will be replaced,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be replaced.
     * <br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     */
    public ReplaceLine() {
        super();
        manipulationWord = MANIPULATION_WORD;
    }

    /**
     * Operation to replace one, or more, lines from a text file.
     * The line to be replaced is chosen either based on a regular
     * expression, or by the line number.
     * <br>
     * If the regular expression
     * is set, only the first found to match it will be replaced,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be replaced.
     * <br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     *
     * @param regex the regular expression to identify the line(s) to be replaced
     * @param replacement the replacement line. It is not necessary to add explicitly any
     *                    end of line (EOL) character in the end of the line, that will be done
     *                    automatically, but ONLY if the original line has an EOL character too.
     *                    The same format of EOL character will be preserved.
     *                    If any EOL character is present anywhere in {@code replacement}, it
     *                    will not be removed nor changed.
     */
    public ReplaceLine(String regex,  String replacement) {
        super(regex);
        setReplacement(replacement);
        manipulationWord = MANIPULATION_WORD;
    }

    /**
     * Operation to replace one, or more, lines from a text file.
     * The line to be replaced is chosen either based on a regular
     * expression, or by the line number.
     * <br>
     * If the regular expression
     * is set, only the first found to match it will be replaced,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be replaced.
     * <br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     *
     * @param regex the regular expression to identify the line(s) to be replaced
     * @param replacement the replacement line. It is not necessary to add explicitly any
     *                    end of line (EOL) character in the end of the line, that will be done
     *                    automatically, but ONLY if the original line has an EOL character too.
     *                    The same format of EOL character will be preserved.
     *                    If any EOL character is present anywhere in {@code replacement}, it
     *                    will not be removed nor changed.
     * @param firstOnly if true, only the first line found (from top down) to match
     *                  the regular expression will be replaced. If false, all of them
     *                  will
     */
    public ReplaceLine(String regex, String replacement, boolean firstOnly) {
        super(regex, firstOnly);
        setReplacement(replacement);
        manipulationWord = MANIPULATION_WORD;
    }

    /**
     * Operation to replace one line from a text file, based on a
     * line number.
     *
     * @param replacement the replacement line. It is not necessary to add explicitly any
     *                    end of line (EOL) character in the end of the line, that will be done
     *                    automatically, but ONLY if the original line has an EOL character too.
     *                    The same format of EOL character will be preserved.
     *                    If any EOL character is present anywhere in {@code replacement}, it
     *                    will not be removed nor changed.
     * @param lineNumber the number of the line to be replaced
     */
    public ReplaceLine(Integer lineNumber, String replacement) {
        super(lineNumber);
        setReplacement(replacement);
        manipulationWord = MANIPULATION_WORD;
    }

    /**
     * Sets the replacement line
     *
     * @param replacement the replacement line. It is not necessary to add explicitly any
     *                    end of line (EOL) character in the end of the line, that will be done
     *                    automatically, but ONLY if the original line has an EOL character too.
     *                    The same format of EOL character will be preserved.
     *                    If any EOL character is present anywhere in {@code replacement}, it
     *                    will not be removed nor changed.
     * @return this transformation operation instance
     */
    public ReplaceLine setReplacement(String replacement) {
        checkForNull("Replacement Line", replacement);
        this.replacement = replacement;
        return this;
    }

    public String getReplacement() {
        return replacement;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected boolean manipulateLine(String lineToBeManipulated, Writer writer) throws IOException {
        String eol = EolHelper.getStartEol(lineToBeManipulated);
        boolean written = false;
        if (eol != null) {
            writer.write(eol);
            written = true;
        }
        if (replacement != null) {
            writer.write(replacement);
            written = true;
        }
        return written;
    }

}
