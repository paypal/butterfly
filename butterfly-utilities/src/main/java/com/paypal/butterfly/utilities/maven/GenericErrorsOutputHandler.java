package com.paypal.butterfly.utilities.maven;

import java.util.regex.Pattern;

/**
 * Reads lines from the Maven execution output and produces as result a String stating the errors, if any.
 *
 * @author mcrockett
 */
public class GenericErrorsOutputHandler implements MavenInvocationOutputHandler<GenericErrorsOutputHandler, String> {

    private static final Pattern GOAL_ERROR_LINE_MATCH_REGEX = Pattern.compile("\\[ERROR\\][\\s]*Failed to execute goal.*");
    private static final Pattern PROJECT_ERROR_LINE_MATCH_REGEX = Pattern.compile("\\[ERROR\\][\\s]*The project.*has [\\d]+ error.*");
    private static final Pattern GENERIC_ERROR_LINE_MATCH_REGEX = Pattern.compile("^Error:.*");
    private static final String ERROR_PHRASE = "[ERROR] ";
    private static final String MSG_FORMAT = "%s %s";

    private boolean executionStarted = false;
    private String message = "";
    private boolean isProjectError = false;

    /**
     * Creates the error message from summary and details.
     *
     * @param summary summary of the error
     * @param details more details on the error
     * @return a String representation of the summary and details
     */
    private static String createMessage(String summary, String details) {
        return String.format(MSG_FORMAT, summary, details);
    }

    /**
     * Removes the log level and trims the string.
     *
     * @param line the line to be processed
     * @return a String with log level removed and trimmed.
     */
    private static String removeLogLevel(String line) {
        return line.substring(ERROR_PHRASE.length()).trim();
    }

    @Override
    public void consumeLine(String line) {
        executionStarted = true;
        if (GOAL_ERROR_LINE_MATCH_REGEX.matcher(line).matches()) {
            message = removeLogLevel(line);
        } else if (PROJECT_ERROR_LINE_MATCH_REGEX.matcher(line).matches()) {
            message = removeLogLevel(line);
            isProjectError = true;
        } else if (isProjectError) {
            message = createMessage(message, removeLogLevel(line));
        } else if (GENERIC_ERROR_LINE_MATCH_REGEX.matcher(line).matches()) {
            message = message.concat(line);
        }
    }

    @Override
    public String getResult() {
        if (!executionStarted) {
            throw new IllegalStateException("Execution has not started. No results to return.");
        } else {
            return message;
        }
    }

    @Override
    public GenericErrorsOutputHandler copy() {
        return new GenericErrorsOutputHandler();
    }

}

