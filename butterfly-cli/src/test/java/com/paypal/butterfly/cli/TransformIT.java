package com.paypal.butterfly.cli;

import com.test.BlankTemplate;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Integration tests for Butterfly CLI when running a transformation.
 *
 * IMPORTANT: this test requires running separately from {@link MiscIT},
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
public class TransformIT {

    // TODO Add a tests for: verbose, debug, zip, same folder, custom output folder

    @Test
    public void transformTest() throws IOException, URISyntaxException {

        // Capturing the console output
        PrintStream systemOut = System.out;
        File transformOut = Files.createTempFile("butterfly-cli-transform-output-", null).toFile();
        PrintStream transformStream = new PrintStream(transformOut);
        System.setOut(transformStream);

        // Running a dummy transformation
        String sampleAppPath = new File(this.getClass().getResource("/sample_app").toURI()).getAbsolutePath();
        ButterflyCliRun run = new ButterflyCliApp().run(sampleAppPath, "-r", "out/result-transform.json");

        // Closing captured console output stream, and restoring original system out
        transformStream.close();
        System.setOut(systemOut);

        // Ensuring transformation metadata is correct
        assertEquals(run.getInputArguments(), new String[]{sampleAppPath, "-r", "out/result-transform.json"});
        assertEquals(run.getExitStatus(), 0, "Transformation failed: " + run.getErrorMessage());
        assertEquals(run.getButterflyVersion(), "TEST");
        assertNull(run.getErrorMessage());
        assertNull(run.getExceptionMessage());
        assertEquals(run.getExtensions(), Collections.emptyList());

        // Ensuring console output, logfile and result JSON file are as expected
        consoleOutputTest(run, transformOut);
        logFileTest(run);
        jsonResultTest(run);
    }

    @Test(dependsOnMethods = "transformTest")
    public void transformSameFolderBlankTemplateTest() throws IOException, URISyntaxException {
        File sampleApp = new File(this.getClass().getResource("/sample_app").toURI());

        File sampleAppCopy = Files.createTempDirectory("butterfly-blank-test-app", new FileAttribute[]{}).toFile();
        FileUtils.copyDirectory(sampleApp, sampleAppCopy);

        File testFile = new File(this.getClass().getResource("/butterfly.properties").toURI());
        ButterflyCliRun run = new ButterflyCliApp().run(sampleAppCopy.getAbsolutePath(), "-f", "-t", BlankTemplate.class.getName());

        assertEquals(run.getExitStatus(), 0);
        assertEquals(sampleAppCopy.listFiles().length, 1);
        assertEquals(sampleAppCopy.listFiles()[0].getName(), testFile.getName());
        assertTrue(FileUtils.contentEquals(sampleAppCopy.listFiles()[0], testFile));
    }

    private void consoleOutputTest(ButterflyCliRun run, File transformOut) throws URISyntaxException, IOException {
        Map<Character, String> baselineTransformValues = new HashMap<>();
        baselineTransformValues.put('1', run.getTransformationResult().getTransformedApplicationDir().getAbsolutePath());
        baselineTransformValues.put('2', run.getLogFile().getAbsolutePath());

        File templateFile = new File(this.getClass().getResource("/transformOut.txt").toURI());
        File baselineTransform = SimpleInterpolator.generate(templateFile, baselineTransformValues);
        assertTrue(FileUtils.contentEquals(baselineTransform, transformOut), "Generated output differs from test baseline\nTest baseline: " + baselineTransform + "\nGenerated output: " + transformOut + "\n");
    }

    private void logFileTest(ButterflyCliRun run) {
        File logFile = run.getLogFile();

        assertNotNull(run.getLogFile());
        assertTrue(logFile.exists());
        assertTrue(logFile.isFile());
        assertTrue(logFile.length() > 0);
        assertEquals(logFile.getParent(), new File(System.getProperty("user.dir"), "logs").getAbsolutePath());
        assertTrue(logFile.getName().matches("sample_app_\\d*\\.log"));
    }

    private void jsonResultTest(ButterflyCliRun run) throws IOException, URISyntaxException {
        File resultFile = new File(System.getProperty("user.dir"), "out/result-transform.json");

        assertTrue(resultFile.exists());
        assertTrue(resultFile.isFile());
        assertTrue(resultFile.length() > 0);
        assertEquals(resultFile.getName(), "result-transform.json");

        Map<Character, String> baselineResultValues = new HashMap<>();
        baselineResultValues.put('a', run.getLogFile().getAbsolutePath());
        baselineResultValues.put('b', run.getTransformationResult().getId());
        baselineResultValues.put('c', run.getTransformationResult().getTransformationRequest().getId());
        baselineResultValues.put('d', String.valueOf(run.getTransformationResult().getTransformationRequest().getTimestamp()));
        baselineResultValues.put('e', run.getTransformationResult().getTransformationRequest().getDateTime());
        baselineResultValues.put('f', String.valueOf(run.getTransformationResult().getTimestamp()));
        baselineResultValues.put('g', run.getTransformationResult().getDateTime());
        baselineResultValues.put('h', run.getTransformationResult().getTransformedApplicationDir().getAbsolutePath());
        baselineResultValues.put('i', run.getTransformationResult().getMetrics().get(0).getDateTime());
        baselineResultValues.put('j', String.valueOf(run.getTransformationResult().getMetrics().get(0).getTimestamp()));
        baselineResultValues.put('l', String.valueOf(run.getTransformationResult().getTransformationRequest().getApplication().getFolder().getAbsolutePath()));
        baselineResultValues.put('m', String.valueOf(System.getProperty("user.name")));

        File templateFile = new File(this.getClass().getResource("/result-transform.json").toURI());
        File baselineResult = SimpleInterpolator.generate(templateFile, baselineResultValues);
        assertTrue(FileUtils.contentEquals(baselineResult, resultFile), "Generated JSON result differs from test baseline\nTest baseline: " + baselineResult + "\nGenerated result: " + resultFile + "\n");
    }

}
