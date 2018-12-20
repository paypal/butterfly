package com.paypal.butterfly.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.cli.ButterflyCliApp;
import com.paypal.butterfly.cli.ButterflyCliRun;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * Assert class to test Butterfly transformations,
 * making sure applications were transformed properly
 *
 * @author facarvalho
 */
public abstract class Assert {

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

        File transformedApps = Files.createTempDir();

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

            run = new ButterflyCliApp().run(arguments);
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

        Logger logger = LoggerFactory.getLogger(Assert.class);

        logger.info("Comparing the following folders:");
        logger.info("Baseline application: {}", baselineApplication.getAbsolutePath());
        logger.info("Transformed application: {}", transformedApplication.getAbsolutePath());

        FoldersComparison.assertEquals(baselineApplication, transformedApplication, xmlSemanticComparison);
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

        File transformedApps = Files.createTempDir();

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

            run = new ButterflyCliApp().run(arguments);
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

}
