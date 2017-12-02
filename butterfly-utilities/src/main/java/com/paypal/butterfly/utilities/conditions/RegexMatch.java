package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.SingleCondition;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Checks if a regular expression matches any line in the specified text file.
 * It returns true only if the specified text file has one or more lines that match
 * the given regular expression.
 *
 * @author facarvalho
 */
public class RegexMatch extends SingleCondition<RegexMatch> {

    private static final String DESCRIPTION = "Check if regular expression '%s' matches against any line in file %s";

    private String regex;

    /**
     * Checks if a regular expression matches any line in the specified text file.
     */
    public RegexMatch() {
    }

    /**
     * Checks if a regular expression matches any line in the specified text file.
     *
     * @param regex the regular expression to be evaluated against the specified text file
     */
    public RegexMatch(String regex) {
        setRegex(regex);
    }

    /**
     * Sets the regular expression to be evaluated against the specified text file
     *
     * @param regex the regular expression to be evaluated against the specified text file
     * @return this utility instance
     */
    public RegexMatch setRegex(String regex) {
        checkForBlankString("regex", regex);
        this.regex = regex;
        return this;
    }

    /**
     * Returns the regular expression to be evaluated against the specified text file
     *
     * @return the regular expression to be evaluated against the specified text file
     */
    public String getRegex() {
        return regex;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, regex, getRelativePath());
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File file = getAbsoluteFile(transformedAppFolder, transformationContext);

        if (!file.exists()) {
            // TODO Should this be done as pre-validation?
            FileNotFoundException ex = new FileNotFoundException("File to be evaluated has not been found");
            return TUExecutionResult.error(this, ex);
        }

        BufferedReader reader = null;
        boolean evalResult = false;
        TUExecutionResult result = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            evalResult = evaluate(reader);
            result = TUExecutionResult.value(this, evalResult);
        } catch (Exception e) {
            result = TUExecutionResult.error(this,  e);
        } finally {
            if(reader != null) try {
                reader.close();
            } catch (IOException e) {
                if (result != null) {
                    result.addWarning(e);
                }
            }
        }

        return result;
    }

    private boolean evaluate(BufferedReader reader) throws IOException {
        String currentLine;
        boolean evalResult = false;
        final Pattern pattern = Pattern.compile(regex);
        while((currentLine = reader.readLine()) != null) {
            if(pattern.matcher(currentLine).matches()) {
                evalResult = true;
                break;
            }
        }

        return evalResult;
    }

}
