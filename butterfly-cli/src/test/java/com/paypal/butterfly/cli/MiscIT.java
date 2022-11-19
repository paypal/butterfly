package com.paypal.butterfly.cli;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Collections;

import static org.testng.Assert.*;

/**
 * Integration tests for Butterfly CLI when running non-transformation options
 *
 * IMPORTANT: this test requires running separately from {@link TransformIT},
 * since both tests rely on static members in the Butterfly CLI project.
 * That is natural and acceptable, since the CLI is supposed to be only
 * single-threaded and executed as a batch operation. In order to guarantee
 * that during integration tests, Gradle has been configured
 * (using test {forkEvery = 1}) to use one JVM per test class.
 * Having said that, whenever running Butterfly CLI tests from an IDE, make
 * sure you don't run them all under the same JVM. You can easily do so by
 * running each integration test class individually.
 *
 * @author facarvalho
 */
public class MiscIT {

    @Test
    public void helpTest() throws IOException, URISyntaxException {

        ButterflyCliRun run = new ButterflyCliApp().run();
        assertEquals(run.getExitStatus(), 0);

        // Ensuring run metadata is correct
        assertEquals(run.getInputArguments(), new String[]{});
        assertEquals(run.getButterflyVersion(), "TEST");
        assertNull(run.getErrorMessage());
        assertNull(run.getExceptionMessage());
        assertEquals(run.getExtensions(), Collections.emptyList());
        assertNull(run.getLogFile());

        // Capturing the console output
        PrintStream systemOut = System.out;
        File helpOut = Files.createTempFile("butterfly-cli-help-output", null).toFile();
        PrintStream helpStream = new PrintStream(helpOut);
        System.setOut(helpStream);

        // Running help option three times (in different ways) to capture console output
        assertEquals(new ButterflyCliApp().run().getExitStatus(), 0);
        assertEquals(new ButterflyCliApp().run("-h").getExitStatus(), 0);
        assertEquals(new ButterflyCliApp().run("-?").getExitStatus(), 0);

        // Closing captured console output stream, and restoring original system out
        helpStream.close();
        System.setOut(systemOut);

        // Ensuring console output is as expected
        File helpBaselineOut = new File(this.getClass().getResource("/helpOut.txt").toURI());
        assertTrue(FileUtils.contentEquals(helpBaselineOut, helpOut), "Generated help differs from test baseline\nTest baseline: " + helpBaselineOut + "\nGenerated result: " + helpOut + "\n");
    }

    @Test(dependsOnMethods = "helpTest")
    public void extensionsListTest() throws IOException, URISyntaxException {

        // Capturing the console output
        PrintStream systemOut = System.out;
        File listOut = Files.createTempFile("butterfly-cli-list-output", null).toFile();
        PrintStream listStream = new PrintStream(listOut);
        System.setOut(listStream);

        ButterflyCliRun run = new ButterflyCliApp().run("-l", "-r", "out/result-list.json");

        // Closing captured console output stream, and restoring original system out
        listStream.close();
        System.setOut(systemOut);

        // Ensuring run metadata is correct
        assertEquals(run.getExitStatus(), 0, run.getExceptionMessage());
        assertEquals(run.getInputArguments(), new String[]{"-l", "-r", "out/result-list.json"});
        assertEquals(run.getButterflyVersion(), "TEST");
        assertNull(run.getErrorMessage());
        assertNull(run.getExceptionMessage());
        assertEquals(run.getExtensions().size(), 2);
        assertNull(run.getLogFile());

        // Ensuring console output is as expected
        File listBaselineOut = new File(this.getClass().getResource("/extensionsListOut.txt").toURI());
        assertTrue(FileUtils.contentEquals(listBaselineOut, listOut), "Generated extensions list differs from test baseline\nTest baseline: " + listBaselineOut + "\nGenerated result: " + listOut + "\n");

        // Ensuring result JSON file is as expected
        jsonResultTest(run);
    }

    private void jsonResultTest(ButterflyCliRun run) throws IOException, URISyntaxException {
        File resultFile = new File(System.getProperty("user.dir"), "out/result-list.json");

        assertTrue(resultFile.exists());
        assertTrue(resultFile.isFile());
        assertTrue(resultFile.length() > 0);
        assertEquals(resultFile.getName(), "result-list.json");

        File baselineResult =  new File(this.getClass().getResource("/result-list.json").toURI());
        assertTrue(FileUtils.contentEquals(baselineResult, resultFile), "Generated JSON result differs from test baseline\nTest baseline: " + baselineResult + "\nGenerated result: " + resultFile + "\n");
    }

}
