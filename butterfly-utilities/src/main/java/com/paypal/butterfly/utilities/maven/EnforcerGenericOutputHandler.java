package com.paypal.butterfly.utilities.maven;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * EnforcerErrorOutputHandler read lines from the maven enforcer plugin output,
 * and creates a set of strings indicating the validation failures.
 *
 * @author mcrockett
 */
public class EnforcerGenericOutputHandler implements MavenInvocationOutputHandler<Set<String>> {
    private static final Pattern RULE_WARNING_LINE_MATCH_REGEX = Pattern.compile(".* Rule [\\d]+:.*");
    private static final String RULE_BEGINNING_PHRASE = "Rule ";

    private boolean executionStarted = false;
    private String message = null;
    private Set<String> errorMessages = new HashSet<String>();

    /**
     * Creates the error message from summary and details.
     *
     * @param summary summary of the error
     * @param details more details on the error
     * @return a String representation of the summary and details
     */
    public static String createMessage(String summary, String details) {
        return String.format("%s '%s'.", summary, details);
    }

    @Override
    public void consumeLine(String line) {
        executionStarted = true;
        if (true == RULE_WARNING_LINE_MATCH_REGEX.matcher(line).matches()) {
            int ruleLocation = line.indexOf(RULE_BEGINNING_PHRASE);

            message = line.substring(ruleLocation);
        } else if (null != message) {
            errorMessages.add(createMessage(message, line));
            message = null;
        }
    }

    @Override
    public Set<String> getResult() {
        if (false == executionStarted) {
            throw new IllegalStateException("Execution has not started. No results to return.");
        } else {
            return Collections.unmodifiableSet(errorMessages);
        }
    }

}

