package com.paypal.butterfly.utilities.conditions.pom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.paypal.butterfly.extensions.api.SingleCondition;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

/**
 * Checks if a Maven module has at least one Java class
 * that matches a given regular expression.
 *
 * @author spetratos, facarvalho
 */
public class PomJavaMatch extends SingleCondition<PomJavaMatch> {

    private static final String DESCRIPTION = "Check if a Maven module %s has at least one Java class that matches regular expression %s";

    private String regex;
    private boolean includeMain = true;
    private boolean includeTest = true;

    /**
     * Checks if a Maven module has at least one Java class
     * that matches a given regular expression.
     */
    public PomJavaMatch() {
    }

    /**
     * Checks if a Maven module has at least one Java class
     * that matches a given regular expression.
     *
     * @param regex the regular expression to be evaluated against Java classes under given Maven module
     */
    public PomJavaMatch(String regex) {
        setRegex(regex);
    }

    /**
     * Sets the regular expression to be evaluated against Java classes under given Maven module
     *
     * @param regex the regular expression to be evaluated against Java classes under given Maven module
     * @return this utility instance
     */
    public PomJavaMatch setRegex(String regex) {
        checkForBlankString("regex", regex);
        this.regex = regex;
        return this;
    }

    /**
     * Sets whether Java classes under src/main/java should be evaluated or not.
     * Default value is true.
     *
     * @param includeMain whether Java classes under src/main/java should be evaluated or not
     * @return this utility instance
     */
    public PomJavaMatch setIncludeMain(boolean includeMain) {
        this.includeMain = includeMain;
        return this;
    }

    /**
     * Sets whether Java classes under src/test/java should be evaluated or not.
     * Default value is true.
     *
     * @param includeTest whether Java classes under src/test/java should be evaluated or not
     * @return this utility instance
     */
    public PomJavaMatch setIncludeTest(boolean includeTest) {
        this.includeTest = includeTest;
        return this;
    }

    /**
     * Returns the regular expression to be evaluated against Java classes under given Maven module
     *
     * @return the regular expression to be evaluated against Java classes under given Maven module
     */
    public String getRegex() {
        return regex;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath(), regex);
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);

        List<File> warnings = new ArrayList<>();
        boolean result = false;

        if (includeMain) {
            File javaMainFolder = new File(pomFile.getParentFile(), "src/main/java");
            result = checkJavaFolder(javaMainFolder, warnings);
        }
        if (!result && includeTest) {
            File javaUnitTestFolder = new File(pomFile.getParentFile(), "src/test/java");
            result = checkJavaFolder(javaUnitTestFolder, warnings);
        }

        TUExecutionResult tuExecutionResult;
        if (warnings.isEmpty()) {
            tuExecutionResult = TUExecutionResult.value(this, result);
        } else {
            TransformationUtilityException e = new TransformationUtilityException("The following Java files could not be read: " + warnings);
            tuExecutionResult = TUExecutionResult.warning(this, e, result);
        }
        return tuExecutionResult;
    }

    private boolean checkJavaFolder(File javaFolder, List<File> warnings) {
        boolean result = false;
        if (javaFolder.exists()) {
            Pattern pattern = Pattern.compile(regex);
            result = FileUtils.listFiles(javaFolder, new String[] { "java" }, true).stream().filter(j -> {
                try {
                    return Files.lines(j.toPath()).map(pattern::matcher).filter(Matcher::matches).findFirst().isPresent();
                } catch (IOException e) {
                    warnings.add(j);
                    return false;
                }
            }).findFirst().isPresent();
        }
        return result;
    }

}
