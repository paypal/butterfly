package com.paypal.butterfly.test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.io.Files;
import com.paypal.butterfly.cli.ButterflyCliApp;
import com.paypal.butterfly.cli.ButterflyCliRun;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.api.TransformationResult;

/**
 * Assert class to test Butterfly transformations,
 * making sure applications were transformed properly
 *
 * @author facarvalho
 */
public abstract class Assert {

    private static final Logger logger = LoggerFactory.getLogger(Assert.class);

    private static File transformedApps;
    private static Throwable transformedAppsThrowable;
    private static DocumentBuilder builder;
    private static ParserConfigurationException xmlParserConfigurationException;

    static {
        try {
            transformedApps = Files.createTempDir();
        } catch (Throwable t) {
            transformedAppsThrowable = t;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            xmlParserConfigurationException = e;
        }
    }

    /**
     * Transforms {@code originalApplication} using {@code transformationTemplate} and compares the generated transformed application
     * with a baseline application {@code baselineApplication}, failing the assertion if their content differ.
     * Besides source code comparison, it also returns a {@link TransformationResult} object, which, can be used to assert in details
     * this transformation generated the expected result and metrics.
     * Notice that this transformation happens in a separated and temporary folder, keeping the original application folder preserved.
     *
     * @param baselineApplication a folder containing a baseline manually transformed application, to be compared against the generated transformed application
     * @param originalApplication the original application, to be transformed
     * @param transformationTemplate the transformation template used to transform the original application
     * @return a {@link TransformationResult} object, which can be used to assert in details this transformation generated the expected result and metrics
     */
    public static TransformationResult assertTransformation(File baselineApplication, File originalApplication, Class<? extends TransformationTemplate> transformationTemplate) {
        return assertTransformation(baselineApplication, originalApplication, transformationTemplate, null, null, null, false);
    }

    /**
     * Transforms {@code originalApplication} using {@code transformationTemplate} and compares the generated transformed application
     * with a baseline application {@code baselineApplication}, failing the assertion if their content differ.
     * Besides source code comparison, it also returns a {@link TransformationResult} object, which, can be used to assert in details
     * this transformation generated the expected result and metrics.
     * Notice that this transformation happens in a separated and temporary folder, keeping the original application folder preserved.
     *
     * @param baselineApplication a folder containing a baseline manually transformed application, to be compared against the generated transformed application
     * @param originalApplication the original application, to be transformed
     * @param transformationTemplate the transformation template used to transform the original application
     * @param verbose if true, runs Butterfly in verbose mode, printing log messages not just in a log file, but also on the console
     * @param debug if true, runs Butterfly in debug mode
     * @param version the version the application should be upgraded to. This option only makes sense if the transformation template to be used is also an upgrade template.
     *                If not, it is ignored. If it is, but this option is not specified, then the application will be upgraded all the way to the latest version possible
     * @param xmlSemanticComparison if true, compare XML files semantically, ignoring indentation, comments and formatting differences, as opposed to a binary comparison. Notice though that, if the XML file is not well formed, a binary comparison will be done regardless of {@code xmlSemanticComparison}.
     * @return a {@link TransformationResult} object, which can be used to assert in details this transformation generated the expected result and metrics
     */
    public static TransformationResult assertTransformation(File baselineApplication, File originalApplication, Class<? extends TransformationTemplate> transformationTemplate, Boolean verbose, Boolean debug, String version, boolean xmlSemanticComparison) {
        if (baselineApplication == null || !baselineApplication.exists() || !baselineApplication.isDirectory()) {
            throw new IllegalArgumentException("Baseline application file is null, does not exist or is not a directory: " + (baselineApplication == null ? "null" : baselineApplication.getAbsolutePath()));
        }
        if (originalApplication == null || !originalApplication.exists() || !originalApplication.isDirectory()) {
            throw new IllegalArgumentException("Original application file is null, does not exist or is not a directory: " + (originalApplication == null ? "null" : originalApplication.getAbsolutePath()));
        }
        if (!transformedApps.exists() || !transformedApps.isDirectory()) {
            if (transformedAppsThrowable != null) {
                throw new IllegalStateException("Temporary transformation directory could not be created", transformedAppsThrowable);
            } else if (!transformedApps.canWrite()) {
                throw new IllegalStateException("Temporary transformation directory could not be created, no permission to write at: " + transformedApps.getAbsolutePath());
            } else {
                throw new IllegalStateException("Temporary transformation directory could not be created: " + transformedApps.getAbsolutePath());
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
                String stackTrace;
                try {
                    stackTrace = "\n" + run.getTransformationResult().getAbortDetails().getExceptionStackTrace();
                } catch (NullPointerException e) {
                    stackTrace = "";
                }
                throw new AssertionError(errorMessage + " \n" + exceptionMessage + stackTrace);
            }
        }

        File sampleAppTransformed = run.getTransformationResult().getTransformedApplicationDir();
        assertTransformation(baselineApplication, sampleAppTransformed, xmlSemanticComparison);

        return run.getTransformationResult();
    }

    /**
     * Compares the generated transformed application, whose code is supposed to be in {@code transformedApplication},
     * with a baseline application {@code baselineApplication}, failing the assertion if their content differ.
     *
     * @param baselineApplication a folder containing a baseline manually transformed application, to be compared against the generated transformed application
     * @param transformedApplication a folder containing the automatically transformed application, to be compared against the baseline application
     */
    public static void assertTransformation(File baselineApplication, File transformedApplication) {
        assertTransformation(baselineApplication, transformedApplication, false);
    }

    /**
     * Compares the generated transformed application, whose code is supposed to be in {@code transformedApplication},
     * with a baseline application {@code baselineApplication}, failing the assertion if their content differ.
     *
     * @param baselineApplication a folder containing a baseline manually transformed application, to be compared against the generated transformed application
     * @param transformedApplication a folder containing the automatically transformed application, to be compared against the baseline application
     * @param xmlSemanticComparison if true, compare XML files semantically, ignoring indentation, comments and formatting differences, as opposed to a binary comparison. Notice though that, if the XML file is not well formed, a binary comparison will be done regardless of {@code xmlSemanticComparison}.
     */
    public static void assertTransformation(File baselineApplication, File transformedApplication, boolean xmlSemanticComparison) {
        if (baselineApplication == null || !baselineApplication.exists() || !baselineApplication.isDirectory()) throw new IllegalArgumentException("Specified expected file is null, does not exist or is not a directory");
        if (transformedApplication == null || !transformedApplication.exists() || !transformedApplication.isDirectory()) throw new IllegalArgumentException("Specified actual file is null, does not exist, or is not a directory");

        logger.info("Comparing the following folders:");
        logger.info("Baseline application: {}", baselineApplication.getAbsolutePath());
        logger.info("Transformed application: {}", transformedApplication.getAbsolutePath());

        assertEqualFolderStructure(baselineApplication, baselineApplication, transformedApplication);
        try {
            assertEqualFolderContent(baselineApplication, baselineApplication, transformedApplication, xmlSemanticComparison);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Attempts to transform {@code originalApplication} using {@code transformationTemplate} and assert that transformation fails and is aborted,
     * besides resulting in the expected abort message.
     * It also returns a {@link TransformationResult} object, which can be used to assert in details this transformation generated
     * the expected result, metrics and abort details.
     * Notice that this transformation happens in a separated and temporary folder, keeping the original application folder preserved.
     *
     * @param originalApplication the original application, to be transformed
     * @param transformationTemplate the transformation template used to transform the original application
     * @param expectedAbortMessage the expected abort message, to be compared against the actual
     * @return a {@link TransformationResult} object, which can be used to assert in details this transformation generated the expected result, metrics and abort details.
     */
    public static TransformationResult assertAbort(File originalApplication, Class<? extends TransformationTemplate> transformationTemplate, String expectedAbortMessage) {
        return assertAbort(originalApplication, transformationTemplate, null, null, null, expectedAbortMessage);
    }

    /**
     * Attempts to transform {@code originalApplication} using {@code transformationTemplate} and assert that transformation fails and is aborted,
     * besides resulting in the expected abort message.
     * It also returns a {@link TransformationResult} object, which can be used to assert in details this transformation generated
     * the expected result, metrics and abort details.
     * Notice that this transformation happens in a separated and temporary folder, keeping the original application folder preserved.
     *
     * @param originalApplication the original application, to be transformed
     * @param transformationTemplate the transformation template used to transform the original application
     * @param verbose if true, runs Butterfly in verbose mode, printing log messages not just in a log file, but also on the console
     * @param debug if true, runs Butterfly in debug mode
     * @param version the version the application should be upgraded to. This option only makes sense if the transformation template to be used is also an upgrade template.
     *                If not, it is ignored. If it is, but this option is not specified, then the application will be upgraded all the way to the latest version possible
     * @param expectedAbortMessage the expected abort message, to be compared against the actual
     * @return a {@link TransformationResult} object, which can be used to assert in details this transformation generated the expected result, metrics and abort details.
     */
    public static TransformationResult assertAbort(File originalApplication, Class<? extends TransformationTemplate> transformationTemplate, Boolean verbose, Boolean debug, String version, String expectedAbortMessage) {
        if (originalApplication == null || !originalApplication.exists() || !originalApplication.isDirectory()) {
            throw new IllegalArgumentException("Original application file is null, does not exist or is not a directory: " + (originalApplication == null ? "null" : originalApplication.getAbsolutePath()));
        }
        if (!transformedApps.exists() || !transformedApps.isDirectory()) {
            if (transformedAppsThrowable != null) {
                throw new IllegalStateException("Temporary transformation directory could not be created", transformedAppsThrowable);
            } else if (!transformedApps.canWrite()) {
                throw new IllegalStateException("Temporary transformation directory could not be created, no permission to write at: " + transformedApps.getAbsolutePath());
            } else {
                throw new IllegalStateException("Temporary transformation directory could not be created: " + transformedApps.getAbsolutePath());
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
            String actualAbortMessage = run.getExceptionMessage();
            if ((actualAbortMessage != null && expectedAbortMessage != null && !actualAbortMessage.equals(expectedAbortMessage)) ||
                    (actualAbortMessage == null && expectedAbortMessage != null) || (actualAbortMessage != null && expectedAbortMessage == null ) ) {
                throw new AssertionError("expected [" + expectedAbortMessage + "] but found [" + actualAbortMessage + "]");
            }
        } else {
            throw new AssertionError("Transformation did not abort, it completed successfully. Transformed application can be found at: "
                    + run.getTransformationResult().getTransformedApplicationDir().getAbsolutePath());
        }

        return run.getTransformationResult();
    }

    public static void assertNoWarnings(TransformationResult transformationResult) {
        assertWarnings(transformationResult, 0);
    }

    public static void assertNoErrors(TransformationResult transformationResult) {
        assertErrors(transformationResult, 0);
    }

    public static void assertWarnings(TransformationResult transformationResult, int expectedNumberOfWarnings) {
        long actualNumberOfWarnings = transformationResult.getMetrics().stream()
                .mapToInt(m -> m.getStatistics().getTUExecutionResultWarningCount() + m.getStatistics().getTOExecutionResultWarningCount()).sum();

        if (actualNumberOfWarnings != expectedNumberOfWarnings) {
            throw new AssertionError("expected [" + expectedNumberOfWarnings + "] but found [" + actualNumberOfWarnings + "]");
        }
    }

    public static void assertErrors(TransformationResult transformationResult, int expectedNumberOfErrors) {
        long actualNumberOfErrors = transformationResult.getMetrics().stream()
                .mapToInt(m -> m.getStatistics().getPerformResultErrorCount() + m.getStatistics().getTUExecutionResultErrorCount() + m.getStatistics().getTOExecutionResultErrorCount()).sum();

        if (actualNumberOfErrors != expectedNumberOfErrors) {
            throw new AssertionError("expected [" + expectedNumberOfErrors + "] but found [" + actualNumberOfErrors + "]");
        }
    }

    /*
     * Check if the expected and actual folders have the same number of files and folders, and with same names.
     * This check is done recursively and not evaluate file contents, only name.
     */
    private static void assertEqualFolderStructure(File baselineApplication, File expected, File actual) {

        // All direct files and folders inside `expected`
        File[] expectedFiles = expected.listFiles();

        // All direct folders inside `expected`
        Set<String> expectedDirectories = new HashSet<>();

        // All direct files inside `expected`
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
                    fail("Unexpected folder found: " + getRelativePath(expected, baselineApplication) + fileRelativePath);
                } else {
                    expectedDirectories.remove(fileRelativePath);
                }
            } else {
                if (!expectedNonDirectories.contains(fileRelativePath)) {
                    fail("Unexpected file found: " + getRelativePath(expected, baselineApplication) + fileRelativePath);
                } else {
                    expectedNonDirectories.remove(fileRelativePath);
                }
            }
        }

        if(expectedDirectories.size() > 0) {
            String missingFolder = getRelativePath(expected, baselineApplication) + expectedDirectories.iterator().next();
            fail("Folder missing: " + missingFolder);
        } else if(expectedNonDirectories.size() > 0) {
            String missingFile = getRelativePath(expected, baselineApplication) + expectedNonDirectories.iterator().next();
            fail("File missing: " + missingFile);
        } else {
            Arrays.stream(expected.listFiles(pathName -> pathName.isDirectory()))
                    .forEach(file -> assertEqualFolderStructure(baselineApplication, file, new File(actual, file.getName())));
        }
    }

    /*
     * Given a file and a supposed parent, return the relative path from
     * the parent file to the child file
     */
    private static String getRelativePath(File file, File parent) {
        String filePath = file.getAbsolutePath();
        String parentPath = parent.getAbsolutePath();

        if (filePath.equals(parentPath)) {
            return "";
        }
        if (filePath.equals(parentPath) || !filePath.startsWith(parentPath + File.separatorChar)) {
            throw new IllegalArgumentException("File " + file + " is not a direct nor indirect child of " + parent);
        }

        return filePath.substring(parentPath.length(), filePath.length());
    }

    /*
     * Compare every file under the expected and actual folders and sub-folders making sure their content is the same.
     * This method assumes the expected and actual folders are structurally identical, meaning, they have same number
     * of files and folders and they are named the same
     */
    private static void assertEqualFolderContent(File baselineApplication, File expected, File actual, boolean xmlSemanticComparison) throws IOException {
        for (File expectedFile : expected.listFiles()) {
            File actualFile = new File(actual, expectedFile.getName());
            if (expectedFile.isDirectory()) {
                assertEqualFolderContent(baselineApplication, expectedFile, actualFile, xmlSemanticComparison);
            } else {
                boolean equal;
                if (xmlSemanticComparison && expectedFile.getName().endsWith(".xml")) {
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

    /*
     * Returns true only if both XML files have same content
     */
    private static boolean xmlEqual(File expectedFile, File actualFile) throws IOException {
        if (xmlParserConfigurationException != null) {
            throw new IllegalStateException("XML parser could not be configured", xmlParserConfigurationException);
        }

        boolean file1parsed = false;
        boolean file2parsed = false;

        try {
            Document file1Xml = builder.parse(expectedFile);
            file1parsed = true;
            Document file2Xml = builder.parse(actualFile);
            file2parsed = true;

            file1Xml.normalizeDocument();
            file2Xml.normalizeDocument();

            XMLUnit.setIgnoreAttributeOrder(true);
            XMLUnit.setIgnoreComments(true);
            XMLUnit.setIgnoreWhitespace(true);

            return XMLUnit.compareXML(file1Xml, file2Xml).similar();
        } catch (Exception e) {
            if (file1parsed ^ file2parsed) {
                // This means only one file couldn't be parsed, which means they are not equal
                return false;
            }
            // This means both files couldn't be parsed, so this comparison is being delegated to
            // a regular file comparison
            return Files.equal(expectedFile, actualFile);
        }
    }

    private static void fail(String failureMessage) {
        throw new AssertionError(failureMessage);
    }

}
