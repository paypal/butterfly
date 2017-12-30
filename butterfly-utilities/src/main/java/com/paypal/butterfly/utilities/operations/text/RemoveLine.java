package com.paypal.butterfly.utilities.operations.text;

import java.io.Writer;

/**
 * Removes one, or more, lines from a text file.
 * The line to be removed is chosen either based on a regular
 * expression, or by the line number.
 * <br>
 * If the regular expression
 * is set, only the first line found to match it will be removed,
 * unless {@link #setFirstOnly(boolean)} is set to false, then
 * all lines that match it will be removed.
 * <br>
 * If a regular expression and a line number are both set,
 * the line number will take precedence, and the regular expression
 * will be ignored
 *
 * @author facarvalho
 */
public class RemoveLine extends AbstractLineOperation<RemoveLine> {

    private static final String DESCRIPTION = "Remove line(s) from file %s";

    private static final String MANIPULATION_WORD = "removed";

    /**
     * Operation to remove one, or more, lines from a text file.
     * The line to be removed is chosen either based on a regular
     * expression, or by the line number.
     * <br>
     * If the regular expression
     * is set, only the first found to match it will be removed,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be removed.
     * <br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     */
    public RemoveLine() {
        super();
        manipulationWord = MANIPULATION_WORD;
    }

    /**
     * Operation to remove one, or more, lines from a text file.
     * The line to be removed is chosen either based on a regular
     * expression, or by the line number.
     * <br>
     * If the regular expression
     * is set, only the first found to match it will be removed,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be removed.
     * <br>
     * If a regular expression and a line number are both set,
     * the line number will take precedence, and the regular expression
     * will be ignored
     *
     * @param regex the regular expression to identify the line(s) to be removed
     */
    public RemoveLine(String regex) {
        super(regex);
        manipulationWord = MANIPULATION_WORD;
    }

    /**
     * Operation to remove one, or more, lines from a text file.
     * The line to be removed is chosen either based on a regular
     * expression, or by the line number.
     * <br>
     * If the regular expression
     * is set, only the first found to match it will be removed,
     * unless {@link #setFirstOnly(boolean)} is set to false, then
     * all lines that match it will be removed.
     * <br>
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
        super(regex, firstOnly);
        manipulationWord = MANIPULATION_WORD;
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

    @Override
    protected boolean manipulateLine(String lineToBeManipulated, Writer writer) {
        return false;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

}
