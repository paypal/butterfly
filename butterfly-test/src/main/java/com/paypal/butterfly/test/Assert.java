package com.paypal.butterfly.test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.io.Files;
import com.paypal.butterfly.cli.ButterflyCliApp;
import com.paypal.butterfly.cli.ButterflyCliRun;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

public abstract class Assert {

    private static File transformedApps;
    private static Throwable transformedAppsThrowable;

    static {
        try {
            transformedApps = Files.createTempDir();
        } catch (Throwable t) {
            transformedAppsThrowable = t;
        }
    }

    /**
     * Transforms {@code originalApplication} using {@code transformationTemplate} and compares the generated transformed application
     * with a baseline application {@code baselineApplication}, failing the assertion if their content differ
     *
     * @param baselineApplication a folder containing a baseline manually transformed application, to be compared against the generated transformed application
     * @param originalApplication the original application, to be transformed
     * @param transformationTemplate the transformation template used to transform the original application
     */
    public static void assertTransformation(File baselineApplication, File originalApplication, Class<? extends TransformationTemplate> transformationTemplate) {
        assertTransformation(baselineApplication, originalApplication, transformationTemplate, null, null, null);
    }

    /**
     * Transforms {@code originalApplication} using {@code transformationTemplate} and compares the generated transformed application
     * with a baseline application {@code baselineApplication}, failing the assertion if their content differ
     *
     * @param baselineApplication a folder containing a baseline manually transformed application, to be compared against the generated transformed application
     * @param originalApplication the original application, to be transformed
     * @param transformationTemplate the transformation template used to transform the original application
     * @param verbose if true, runs Butterfly in verbose mode, printing log messages not just in a log file, but also on the console
     * @param debug if true, runs Butterfly in debug mode
     * @param version the version the application should be upgraded to. This option only makes sense if the transformation template to be used is also an upgrade template.
     *                If not, it is ignored. If it is, but this option is not specified, then the application will be upgraded all the way to the latest version possible
     */
    public static void assertTransformation(File baselineApplication, File originalApplication, Class<? extends TransformationTemplate> transformationTemplate, Boolean verbose, Boolean debug, String version) {
        if (baselineApplication == null || !baselineApplication.exists() || !baselineApplication.isDirectory()) {
            throw new IllegalArgumentException("Baseline application file is null, does not exist or is not a directory: " + (baselineApplication == null ? "null" : baselineApplication.getAbsolutePath()));
        }
        if (originalApplication == null || !originalApplication.exists() || !originalApplication.isDirectory()) {
            throw new IllegalArgumentException("Original application file is null, does not exist or is not a directory: " + (originalApplication == null ? "null" : originalApplication.getAbsolutePath()));
        }
        if (!transformedApps.exists() || !transformedApps.isDirectory()) {
            if (transformedAppsThrowable != null) {
                throw new AssertionError("Temporary transformation directory could not be created", transformedAppsThrowable);
            } else if (!transformedApps.canWrite()) {
                throw new AssertionError("Temporary transformation directory could not be created, no permission to write at: " + transformedApps.getAbsolutePath());
            } else {
                throw new AssertionError("Temporary transformation directory could not be created: " + transformedApps.getAbsolutePath());
            }
        }

        ButterflyCliRun run;
        try {
            List<String> argumentsList = new ArrayList<>();

            argumentsList.add(originalApplication.getAbsolutePath());
            argumentsList.add("-o");
            argumentsList.add(transformedApps.getAbsolutePath());
            argumentsList.add("-t");
            argumentsList.add(transformationTemplate.getName());

            if (verbose != null && verbose) argumentsList.add("-v");
            if (debug != null && debug) argumentsList.add("-d");
            if (StringUtils.isNotBlank(version)) {
                argumentsList.add("-u");
                argumentsList.add(version);
            }

            String[] arguments = new String[argumentsList.size()];
            arguments = argumentsList.toArray(arguments);

            run = ButterflyCliApp.run(arguments);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        if (run.getExitStatus() != 0) {
            String errorMessage = run.getErrorMessage();
            String exceptionMessage = run.getExceptionMessage();
            if (exceptionMessage == null) {
                throw new AssertionError(errorMessage);
            } else {
                throw new AssertionError(errorMessage + " \n" + exceptionMessage);
            }
        }

        File sampleAppTransformed = new File(run.getTransformedApplication());
        assertTransformation(baselineApplication, sampleAppTransformed);
    }

    /**
     * Compares the generated transformed application {@code transformedApplication}
     * with a baseline application {@code baselineApplication}, failing the assertion if their content differ
     *
     * @param baselineApplication a folder containing a baseline manually transformed application, to be compared against the generated transformed application
     * @param transformedApplication a folder containing the automatically transformed application, to be compared against the baseline application
     */
    public static void assertTransformation(File baselineApplication, File transformedApplication) {
        if (baselineApplication == null || !baselineApplication.exists() || !baselineApplication.isDirectory()) throw new IllegalArgumentException("Specified expected file is null, does not exist or is not a directory");
        if (transformedApplication == null || !transformedApplication.exists() || !transformedApplication.isDirectory()) throw new IllegalArgumentException("Specified actual file is null, does not exist, or is not a directory");

        assertEqualFolderStructure(baselineApplication, baselineApplication, transformedApplication);
        try {
            assertEqualFolderContent(baselineApplication, baselineApplication, transformedApplication);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new AssertionError(e);
        }
    }

    private static void assertEqualFolderStructure(File baselineApplication, File expected, File actual) {
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
            fail(String.format("%d directories missing at %s", expectedDirectories.size(), getRelativePath(expected, baselineApplication)));
        } else if(expectedNonDirectories.size() > 0) {
            fail(String.format("%d files missing at %s", expectedNonDirectories.size(), getRelativePath(expected, baselineApplication)));
        } else {
            for (File file : expected.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            })) {
                assertEqualFolderStructure(baselineApplication, file, new File(actual, file.getName()));
            }
        }
    }

    private static String getRelativePath(File file, File parent) {
        String filePath = file.getAbsolutePath();
        String parentPath = parent.getAbsolutePath();

        if (filePath.equals(parentPath) || !filePath.startsWith(parentPath + File.separatorChar)) {
            throw new IllegalArgumentException("Specified file is not a direct nor indirect child of given parent");
        }

        return filePath.substring(parentPath.length(), filePath.length());
    }

    private static void assertEqualFolderContent(File baselineApplication, File expected, File actual) throws IOException, ParserConfigurationException, SAXException {
        for (File expectedFile : expected.listFiles()) {
            File actualFile = new File(actual, expectedFile.getName());
            if (expectedFile.isDirectory()) {
                assertEqualFolderContent(baselineApplication, expectedFile, actualFile);
            } else {
                boolean equal;
                if (expectedFile.getName().endsWith(".xml")) {
                    equal = xmlEqual(expectedFile, actualFile);
                } else {
                    equal = Files.equal(expectedFile, actualFile);
                }
                if(!equal) {
                    fail(String.format("File content is not equal: %s", getRelativePath(expectedFile, baselineApplication)));
                }
            }
        }
    }

    private static boolean xmlEqual(File file1, File file2) throws ParserConfigurationException, IOException, SAXException {
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

    private static void fail(String failureMessage) {
        throw new AssertionError(failureMessage);
    }

}
