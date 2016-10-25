package com.paypal.butterfly.utilities.maven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author mcrockett
 */
public class GenericErrorOutputHandlerTest {
    @Test
    public void hasValidRegularExpression() {
        GenericErrorOutputHandler handler = null;

        String[] validLines = {
                "[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.12.4:test (default-test) on project butterfly-utilities: There are test failures.",
                "[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:2.5.1:compile (default-compile) on project butterfly-utilities: Compilation failure: Compilation failure:",
                "[ERROR]   The project com.paypal.butterfly:butterfly-utilities:1.0.0-SNAPSHOT (/Users/mcrockett/workspaces/butterfly-mcrockett/butterfly-utilities/pom.xml) has 1 error"
        };

        String[] invalidLines = {
                "[ERROR] ailed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.12.4:test (default-test) on project butterfly-utilities: There are test failures.",
                "[ERROR]   The project com.paypal.butterfly:butterfly-utilities:1.0.0-SNAPSHOT (/Users/mcrockett/workspaces/butterfly-mcrockett/butterfly-utilities/pom.xml) has error",
                "[ERROR]   The projec com.paypal.butterfly:butterfly-utilities:1.0.0-SNAPSHOT (/Users/mcrockett/workspaces/butterfly-mcrockett/butterfly-utilities/pom.xml) has 1 error"
        };

        for(int i = 0; i < validLines.length; i += 1) {
            handler = new GenericErrorOutputHandler();
            handler.consumeLine(validLines[i]);
            handler.consumeLine("some error line");
            Assert.assertNotNull(handler.getResult());
        }

        for(int i = 0; i < invalidLines.length; i += 1) {
            handler = new GenericErrorOutputHandler();
            handler.consumeLine(invalidLines[i]);
            handler.consumeLine("some error line");
            Assert.assertNull(handler.getResult());
        }
    }

    @Test
    public void canHaveNullResultsIfNoTriggerFound(){
        GenericErrorOutputHandler handler = new GenericErrorOutputHandler();
        handler.consumeLine("asfasdfa");
        Assert.assertNull(handler.getResult());
    }

    @Test
    public void removesLogLevelAndTrims(){
        String line = "[ERROR]   The project blah blah has 2 errors        ";
        Assert.assertEquals(GenericErrorOutputHandler.removeLogLevel(line), "The project blah blah has 2 errors");
    }

    @Test
    public void getsNextLineIfProjectError(){
        String summary = "[ERROR]   The project blah blah has 2 errors";
        String details = "[ERROR]     'dependencies.dependency.version' for com.github.javaparser:javaparser-core:jar is missing.";
        String summaryOutput = GenericErrorOutputHandler.removeLogLevel(summary);
        String detailsOutput = GenericErrorOutputHandler.removeLogLevel(details);
        GenericErrorOutputHandler handler = new GenericErrorOutputHandler();
        handler.consumeLine(summary);
        handler.consumeLine(details);
        Assert.assertEquals(handler.getResult(), GenericErrorOutputHandler.createMessage(summaryOutput, detailsOutput));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void throwsIfNoResultsBecauseExecutionHasntStarted(){
        GenericErrorOutputHandler handler = new GenericErrorOutputHandler();
        handler.getResult();
    }

    @Test
    public void canHandleSampleOutput() throws IOException{
        GenericErrorOutputHandler handler = new GenericErrorOutputHandler();
        InputStream inputStream = getClass().getResourceAsStream("/sample_maven_output_various_failures.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();

        while (null != line) {
            handler.consumeLine(line);
            line = reader.readLine();
        }

        Assert.assertEquals(handler.getResult(), "Failed to execute goal org.apache.maven.plugins:maven-enforcer-plugin:1.3.1:enforce (enforce) on project client: Some Enforcer rules have failed. Look above for specific messages explaining why the rule failed. -> [Help 1]");
    }
}

