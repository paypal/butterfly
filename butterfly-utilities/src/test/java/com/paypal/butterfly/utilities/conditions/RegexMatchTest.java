package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.util.regex.PatternSyntaxException;

/**
 * Unit tests for {@link RegexMatch}
 *
 * @author facarvalho
 */
public class RegexMatchTest extends TransformationUtilityTestHelper {

    @Test
    public void evalTrueTest() {
        RegexMatch regexMatch;
        TUExecutionResult executionResult;

        regexMatch = new RegexMatch("(.*Mustache.*)").relative("/src/main/resources/dogs.yaml");
        executionResult = regexMatch.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(regexMatch.getRegex(), "(.*Mustache.*)");
        Assert.assertEquals(regexMatch.getDescription(), "Check if regular expression '(.*Mustache.*)' matches against any line in file /src/main/resources/dogs.yaml");
    }

    @Test
    public void evalFalseTest() {
        RegexMatch regexMatch;
        TUExecutionResult executionResult;

        regexMatch = new RegexMatch("(.*Bigode.*)").relative("/src/main/resources/dogs.yaml");
        executionResult = regexMatch.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertEquals(regexMatch.getRegex(), "(.*Bigode.*)");
        Assert.assertEquals(regexMatch.getDescription(), "Check if regular expression '(.*Bigode.*)' matches against any line in file /src/main/resources/dogs.yaml");
    }

    @Test
    public void evalInvalidRegexTest() {
        RegexMatch regexMatch;
        TUExecutionResult executionResult;

        regexMatch = new RegexMatch().relative("/src/main/resources/dogs.yaml");
        regexMatch.setRegex("*Mustache*");
        executionResult = regexMatch.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getException().getClass(), PatternSyntaxException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "Dangling meta character '*' near index 0\n*Mustache*\n^");
    }

    @Test
    public void evalInvalidFileTest() {
        RegexMatch regexMatch;
        TUExecutionResult executionResult;

        regexMatch = new RegexMatch("*Mustache*").relative("/src/main/resources/caes.yaml");
        executionResult = regexMatch.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getException().getClass(), FileNotFoundException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "File to be evaluated has not been found");
    }

}
