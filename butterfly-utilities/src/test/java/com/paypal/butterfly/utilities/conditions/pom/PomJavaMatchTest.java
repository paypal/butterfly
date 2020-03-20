package com.paypal.butterfly.utilities.conditions.pom;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;

/**
 * Unit tests for {@link PomJavaMatch}
 *
 * @author facarvalho
 */
public class PomJavaMatchTest extends TransformationUtilityTestHelper {

    @Test
    public void test() {
        PomJavaMatch pomJavaMatch;
        TUExecutionResult executionResult;

        pomJavaMatch = new PomJavaMatch("(\\s*@ComponentScan\\S*)").setIncludeTest(false).relative("pom.xml");
        Assert.assertEquals(pomJavaMatch.getRegex(), "(\\s*@ComponentScan\\S*)");
        executionResult = pomJavaMatch.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(pomJavaMatch.getDescription(), "Check if a Maven module pom.xml has at least one Java class that matches regular expression (\\s*@ComponentScan\\S*)");

        pomJavaMatch = new PomJavaMatch().setRegex("(\\s*@ComponentScan\\S*)").setIncludeMain(false).setIncludeTest(true).relative("pom.xml");
        executionResult = pomJavaMatch.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertEquals(pomJavaMatch.getDescription(), "Check if a Maven module pom.xml has at least one Java class that matches regular expression (\\s*@ComponentScan\\S*)");
    }

}
