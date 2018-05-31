package com.paypal.butterfly.tests;

import com.google.common.io.Files;
import com.paypal.butterfly.cli.ButterflyCliApp;
import com.paypal.butterfly.cli.ButterflyCliRun;
import org.custommonkey.xmlunit.XMLUnit;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class ButterflyIT {

    private File sampleApp;
    private File sampleAppTransformedBaseline;
    private File transformedApps;
    private ButterflyCliRun run;

    private static final File BUILD_DIR = new File("./build");

    @BeforeClass
    public void setUp() {
        sampleApp = new File("../sample-app");
        sampleAppTransformedBaseline = new File("../sample-app-transformed-baseline");
        transformedApps = new File(BUILD_DIR, "transformed-apps");
        transformedApps.mkdir();
    }

    @Test
    public void helpTest() throws IOException {
        ButterflyCliRun run = ButterflyCliApp.run();
        assertEquals(run.getExitStatus(), 0);

        run = ButterflyCliApp.run("-h");
        assertEquals(run.getExitStatus(), 0);

        run = ButterflyCliApp.run("-?");
        assertEquals(run.getExitStatus(), 0);

        // TODO Find a way to test output
    }

    @Test
    public void extensionsListTest() throws IOException {
        ButterflyCliRun run = ButterflyCliApp.run("-l");
        assertEquals(run.getExitStatus(), 0);

        // TODO Find a way to test output
    }

    @Test
    public void sampleAppRunTest() throws IOException, ParserConfigurationException, SAXException {
        run = ButterflyCliApp.run(sampleApp.getAbsolutePath(), "-o", transformedApps.getAbsolutePath());
        assertEquals(run.getExitStatus(), 0);
    }

    @Test(dependsOnMethods = "sampleAppRunTest")
    public void contentComparisonTest() throws IOException, ParserConfigurationException, SAXException {
        File sampleAppTransformed = new File(run.getTransformedApplication());
        assertEqualFolders(sampleAppTransformedBaseline, sampleAppTransformed);
    }

    @Test
    public void modifyFolderTest() throws IOException, ParserConfigurationException, SAXException {
        File sampleAppCopy = new File(BUILD_DIR, "sample-app-copy");
        FileUtils.copyDirectory(sampleApp, sampleAppCopy);

        run = ButterflyCliApp.run(sampleAppCopy.getAbsolutePath(), "-f");

        assertEquals(run.getExitStatus(), 0);
        assertEqualFolders(sampleAppTransformedBaseline, sampleAppCopy);
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

    private void assertEqualFolders(File expected, File actual) throws IOException, ParserConfigurationException, SAXException {
        if (expected == null || !expected.exists() || !expected.isDirectory()) throw new IllegalArgumentException("Specified expected file is null, does not exist or is not a directory");
        if (actual == null || !actual.exists() || !actual.isDirectory()) throw new IllegalArgumentException("Specified actual file is null, does not exist, or is not a directory");

        assertEqualFolderStructure(expected, actual);
        assertEqualFolderContent(expected, actual);
    }

    private void assertEqualFolderStructure(File expected, File actual) {
        File[] expectedFiles = expected.listFiles();
        Set<String> expectedDirectories = new HashSet<>();
        Set<String> expectedNonDirectories = new HashSet<>();
        String fileRelativePath;
        for (File expectedFile : expectedFiles) {
            fileRelativePath = getRelativePath(expectedFile, expected);
            if (expectedFile.isDirectory()) {
                expectedDirectories.add(fileRelativePath);
            } else {
                expectedNonDirectories.add(fileRelativePath);
            }
        }

        File[] actualFiles = actual.listFiles();
        for (File actualFile : actualFiles) {
            fileRelativePath = getRelativePath(actualFile, actual);
            if (actualFile.isDirectory()) {
                if (!expectedDirectories.contains(fileRelativePath)) {
                    fail("Unexpected folder found: " + fileRelativePath);
                } else {
                    expectedDirectories.remove(fileRelativePath);
                }
            } else {
                if (!expectedNonDirectories.contains(fileRelativePath)) {
                    fail("Unexpected file found: " + fileRelativePath);
                } else {
                    expectedNonDirectories.remove(fileRelativePath);
                }
            }
        }

        if(expectedDirectories.size() > 0) {
            fail(String.format("%d directories missing at %s", expectedDirectories.size(), getRelativePath(expected, sampleAppTransformedBaseline)));
        } else if(expectedNonDirectories.size() > 0) {
            fail(String.format("%d files missing at %s", expectedNonDirectories.size(), getRelativePath(expected, sampleAppTransformedBaseline)));
        } else {
            for (File file : expected.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            })) {
                assertEqualFolderStructure(file, new File(actual, file.getName()));
            }
        }
     }

    private String getRelativePath(File file, File parent) {
        String filePath = file.getAbsolutePath();
        String parentPath = parent.getAbsolutePath();

        if (filePath.equals(parentPath) || !filePath.startsWith(parentPath + File.separatorChar)) {
            throw new IllegalArgumentException("Specified file is not a direct nor indirect child of given parent");
        }

        return filePath.substring(parentPath.length(), filePath.length());
    }

    private void assertEqualFolderContent(File expected, File actual) throws IOException, ParserConfigurationException, SAXException {
        for (File expectedFile : expected.listFiles()) {
            File actualFile = new File(actual, expectedFile.getName());
            if (expectedFile.isDirectory()) {
                assertEqualFolderContent(expectedFile, actualFile);
            } else {
                boolean equal;
                if (expectedFile.getName().endsWith(".xml")) {
                    equal = xmlEqual(expectedFile, actualFile);
                } else {
                    equal = Files.equal(expectedFile, actualFile);
                }
                if(!equal) {
                    fail(String.format("File content is not equal: %s", getRelativePath(expectedFile, sampleAppTransformedBaseline)));
                }
            }
        }
    }

    private boolean xmlEqual(File file1, File file2) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document file1Xml = builder.parse(file1);
        Document file2Xml = builder.parse(file2);

        file1Xml.normalizeDocument();
        file2Xml.normalizeDocument();

        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);

        return XMLUnit.compareXML(file1Xml, file2Xml).similar();
    }

}