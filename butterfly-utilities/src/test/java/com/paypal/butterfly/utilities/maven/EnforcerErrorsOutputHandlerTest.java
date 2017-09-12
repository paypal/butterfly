package com.paypal.butterfly.utilities.maven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author mcrockett
 */
public class EnforcerErrorsOutputHandlerTest {
    @Test
    public void hasValidRegularExpression() {
        EnforcerErrorsOutputHandler handler = null;

        String[] validLines = {
                "[WARNING] Rule 2: org.apache.maven.plugins.enforcer.FailOnLowerVersionOverride failed with message:",
                "[WARNING] Rule 22: org.apache.maven.plugins.enforcer.FailOnLowerVersionOverride failed with message:",
                " Rule 9:"
        };

        String[] invalidLines = {
                "[WARNING] Rule x: org.apache.maven.plugins.enforcer.FailOnLowerVersionOverride failed with message:",
                "[WARNING] Vule 22: org.apache.maven.plugins.enforcer.FailOnLowerVersionOverride failed with message:",
                "[WARNING] org.apache.maven.plugins.enforcer.FailOnLowerVersionOverride Rule 2 failed with message:"
        };

        for(int i = 0; i < validLines.length; i += 1) {
            handler = new EnforcerErrorsOutputHandler();
            handler.consumeLine(validLines[i]);
            handler.consumeLine("some error line");
            Assert.assertEquals(handler.getResult().size(), 1);
        }

        for(int i = 0; i < invalidLines.length; i += 1) {
            handler = new EnforcerErrorsOutputHandler();
            handler.consumeLine(invalidLines[i]);
            handler.consumeLine("some error line");
            Assert.assertEquals(handler.getResult().size(), 0);
        }
    }

    @Test
    public void canHaveNoResultsIfNoTriggerFound(){
        EnforcerErrorsOutputHandler handler = new EnforcerErrorsOutputHandler();
        handler.consumeLine("asfasdfa");
        Set<String> results = handler.getResult();
        Assert.assertEquals(results.size(), 0);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void throwsIfNoResultsBecauseExecutionHasntStarted(){
        EnforcerErrorsOutputHandler handler = new EnforcerErrorsOutputHandler();
        handler.getResult();
    }

    @Test
    public void canHandleSampleOutput() throws IOException{
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            EnforcerErrorsOutputHandler handler = new EnforcerErrorsOutputHandler();
            inputStream = getClass().getResourceAsStream("/sample_maven_output_various_failures.txt");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();

            while (null != line) {
                handler.consumeLine(line);
                line = reader.readLine();
            }

            Set<String> results = handler.getResult();
            String[] expectedResults = {
                    "Rule 2: org.apache.maven.plugins.enforcer.FailOnLowerVersionOverride failed with message: 'Application dependencies' version are lower than the version used by the framework:'.",
                    "Rule 1: org.apache.maven.plugins.enforcer.RequireMavenVersion failed with message: 'Detected Maven Version: 3.1.1 is not in the allowed range 3.3.1.'.",
                    "Rule 0: org.apache.maven.plugins.enforcer.AlwaysFail failed with message: 'Always fails!'."
            };

            Assert.assertEquals(results.size(), expectedResults.length);

            for (String expectedResult : expectedResults) {
                Assert.assertTrue(results.contains(expectedResult));
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }
    }

}

