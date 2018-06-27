package com.paypal.butterfly.integrationtests;

import static com.paypal.butterfly.test.TransformationTest.assertTransformation;
import static org.testng.Assert.assertEquals;

import java.io.*;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import com.google.common.io.Files;
import com.paypal.butterfly.cli.ButterflyCliApp;
import com.paypal.butterfly.cli.ButterflyCliRun;
import com.paypal.butterfly.extensions.springboot.JavaEEToSpringBoot;

public class ButterflyIT {

    private File sampleApp;
    private File sampleAppTransformedBaseline;

    @BeforeClass
    public void setUp() {
        sampleApp = new File("../sample-apps/echo");
        sampleAppTransformedBaseline = new File("../transformed-baseline/echo-JavaEEToSpringBoot");
    }

    @Test
    public void cliConsoleTest() throws IOException, URISyntaxException {
        // First run to flush out logback log statements
        assertEquals(ButterflyCliApp.run().getExitStatus(), 0);

        PrintStream systemOut = System.out;

        File helpOut = File.createTempFile("butterfly-cli-help-output", null);
        PrintStream helpStream = new PrintStream(helpOut);
        System.setOut(helpStream);

        assertEquals(ButterflyCliApp.run().getExitStatus(), 0);
        assertEquals(ButterflyCliApp.run("-h").getExitStatus(), 0);
        assertEquals(ButterflyCliApp.run("-?").getExitStatus(), 0);

        helpStream.close();

        File helpBaselineOut = new File(this.getClass().getResource("/helpOut.txt").toURI());

// TODO
// This would fail because of the version print in the console.
// The solution is to do a static mock of ButterflyProperties.getString("butterfly.version")
//        assertTrue(FileUtils.contentEquals(helpBaselineOut, helpOut), printInvalidOutput(helpOut));

        File listOut = File.createTempFile("butterfly-cli-list-output", null);
        PrintStream listStream = new PrintStream(listOut);
        System.setOut(listStream);

        assertEquals(ButterflyCliApp.run("-l").getExitStatus(), 0);

        listStream.close();
        System.setOut(systemOut);

        File listBaselineOut = new File(this.getClass().getResource("/extensionsListOut.txt").toURI());
// TODO
// This would fail because of the version print in the console.
// The solution is to do a static mock of ButterflyProperties.getString("butterfly.version")
//        assertTrue(FileUtils.contentEquals(listBaselineOut, listOut), printInvalidOutput(listOut));
    }

    private String printInvalidOutput(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("Output file " + file.getAbsolutePath() + " is invalid:\n\n[");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        }
        int i = stringBuilder.length();
        stringBuilder.replace(i -1, i, "]");

        return stringBuilder.toString();
    }

    @Test
    public void sampleAppRunTest() throws IOException {
        assertTransformation(sampleAppTransformedBaseline, sampleApp, JavaEEToSpringBoot.class);
    }

    @Test
    public void modifyFolderTest() throws IOException, ParserConfigurationException, SAXException {
        File sampleAppCopy = Files.createTempDir();
        FileUtils.copyDirectory(sampleApp, sampleAppCopy);

        ButterflyCliRun run = ButterflyCliApp.run(sampleAppCopy.getAbsolutePath(), "-f", "-t", JavaEEToSpringBoot.class.getName());

        assertEquals(run.getExitStatus(), 0);
        assertTransformation(sampleAppTransformedBaseline, sampleAppCopy);
    }

//    @Test(dependsOnMethods = "sampleAppRunTest")
    public void metricsCheckTest() {
        // TODO Verification by analyzing metrics
//        String metricsFile = run.getMetricsFile();
//        assertFalse(StringUtils.isBlank(metricsFile));
    }

//    @Test(dependsOnMethods = "sampleAppRunTest")
    public void functionalExecutionTest() {
        // TODO Verification by starting the application and running its functional tests
    }

}