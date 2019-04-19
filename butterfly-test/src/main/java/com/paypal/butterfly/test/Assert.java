package com.paypal.butterfly.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.paypal.butterfly.api.AbortDetails;
import com.paypal.butterfly.api.ButterflyFacade;
import com.paypal.butterfly.api.Configuration;
import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Properties;

/**
 * Assert class to test Butterfly transformations,
 * making sure applications were transformed properly
 *
 * @author facarvalho
 */
public abstract class Assert {

    private static final String DEBUG_PROPERTY_NAME = "butterfly.test.debug";

    static {
        checkAndSetDebug();
    }

    private static void checkAndSetDebug() {
        String debugProperty = System.getProperty(DEBUG_PROPERTY_NAME, "false");
        boolean debug = debugProperty.equalsIgnoreCase("true");

        if (debug) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.getLogger("com.paypal.butterfly").setLevel(Level.DEBUG);
        }
    }

    /**
     * Transforms {@code originalApplication} using {@code transformationTemplate} and compares the generated transformed application
     * with a baseline application {@code baselineApplication}, failing the assertion if their content differ.
     * Besides source code comparison, it also returns a {@link TransformationResult} object, which, can be used to assert in details
     * this transformation generated the expected result and metrics.
     * Notice that this transformation happens in a separated and temporary folder, keeping the original application folder preserved.
     *
     * @param facade Butterfly facade object, which is supposed to be auto-wired (using spring-test) into the calling class
     * @param baselineApplication a folder containing a baseline manually transformed application, to be compared against the generated transformed application
     * @param originalApplication the original application, to be transformed
     * @param transformationTemplate the transformation template used to transform the original application
     * @return a {@link TransformationResult} object, which can be used to assert in details this transformation generated the expected result and metrics
     */
    public static TransformationResult assertTransformation(ButterflyFacade facade, File  baselineApplication, File originalApplication, Class<? extends TransformationTemplate> transformationTemplate) {
        return assertTransformation(facade, baselineApplication, originalApplication, transformationTemplate, null, null, false);
    }

    /**
     * Transforms {@code originalApplication} using {@code transformationTemplate} and compares the generated transformed application
     * with a baseline application {@code baselineApplication}, failing the assertion if their content differ.
     * Besides source code comparison, it also returns a {@link TransformationResult} object, which, can be used to assert in details
     * this transformation generated the expected result and metrics.
     * Notice that this transformation happens in a separated and temporary folder, keeping the original application folder preserved.
     *
     * @param facade Butterfly facade object, which is supposed to be auto-wired (using spring-test) into the calling class
     * @param baselineApplication a folder containing a baseline manually transformed application, to be compared against the generated transformed application
     * @param originalApplication the original application, to be transformed
     * @param transformationTemplate the transformation template used to transform the original application
     * @param version the version the application should be upgraded to. This option only makes sense if the transformation template to be used is also an upgrade template.
     *                If not, it is ignored. If it is, but this option is not specified, then the application will be upgraded all the way to the latest version possible
     * @param properties a properties object specifying details about the transformation itself.
     *                   These properties help to specialize the
     *                   transformation, for example, determining if certain operations should
     *                   be skipped or not, or how certain aspects of the transformation should
     *                   be executed. The set of possible properties is defined by the used transformation
     *                   extension and template, read the documentation offered by the extension
     *                   for further details. The properties values are defined by the user requesting the transformation.
     *                   Properties are optional, so, if not desired, this parameter can be set to null.
     * @param xmlSemanticComparison if true, compare XML files semantically, ignoring indentation, comments and formatting differences, as opposed to a binary comparison. Notice though that, if the XML file is not well formed, a binary comparison will be done regardless of {@code xmlSemanticComparison}.
     * @return a {@link TransformationResult} object, which can be used to assert in details this transformation generated the expected result and metrics
     */
    public static TransformationResult assertTransformation(ButterflyFacade facade, File  baselineApplication, File originalApplication, Class<? extends TransformationTemplate> transformationTemplate, String version, Properties properties, boolean xmlSemanticComparison) {
        if (baselineApplication == null || !baselineApplication.exists() || !baselineApplication.isDirectory()) {
            throw new IllegalArgumentException("Baseline application file is null, does not exist or is not a directory: " + (baselineApplication == null ? "null" : baselineApplication.getAbsolutePath()));
        }
        if (originalApplication == null || !originalApplication.exists() || !originalApplication.isDirectory()) {
            throw new IllegalArgumentException("Original application file is null, does not exist or is not a directory: " + (originalApplication == null ? "null" : originalApplication.getAbsolutePath()));
        }

        TransformationResult transformationResult = transform(facade, originalApplication, transformationTemplate, version, properties);

        if (!transformationResult.isSuccessful()) {
            AbortDetails abortDetails = transformationResult.getAbortDetails();
            throw new AssertionError(abortDetails);
        }

        File sampleAppTransformed = transformationResult.getTransformedApplicationDir();
        assertTransformation(baselineApplication, sampleAppTransformed, xmlSemanticComparison);

        return transformationResult;
    }

    // Transforms an application (placing it in a temporary folder) and returns the transformation result
    private static TransformationResult transform(ButterflyFacade facade, File originalApplication, Class<? extends TransformationTemplate> transformationTemplate, String version, Properties properties) {
        Configuration configuration;
        try {
            configuration = facade.newConfiguration(properties, Files.createTempDirectory("butterfly-test", new FileAttribute[]{}).toFile(), false);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        return facade.transform(originalApplication, transformationTemplate, version, configuration);
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

        System.out.println("\nComparing the following folders:");
        System.out.println("Baseline application: " + baselineApplication.getAbsolutePath());
        System.out.println("Transformed application: " + transformedApplication.getAbsolutePath());

        FoldersComparison.assertEquals(baselineApplication, transformedApplication, xmlSemanticComparison);

        System.out.println("Folders match, test succeeded");
    }

    /**
     * Attempts to transform {@code originalApplication} using {@code transformationTemplate} and assert that transformation fails and is aborted,
     * besides resulting in the expected abort message.
     * It also returns a {@link TransformationResult} object, which can be used to assert in details this transformation generated
     * the expected result, metrics and abort details.
     * Notice that this transformation happens in a separated and temporary folder, keeping the original application folder preserved.
     *
     * @param facade Butterfly facade object, which is supposed to be auto-wired (using spring-test) into the calling class
     * @param originalApplication the original application, to be transformed
     * @param transformationTemplate the transformation template used to transform the original application
     * @param expectedAbortMessage the expected abort message, to be compared against the actual
     * @return a {@link TransformationResult} object, which can be used to assert in details this transformation generated the expected result, metrics and abort details.
     */
    public static TransformationResult assertAbort(ButterflyFacade facade, File originalApplication, Class<? extends TransformationTemplate> transformationTemplate, String expectedAbortMessage) {
        return assertAbort(facade, originalApplication, transformationTemplate, null, null, expectedAbortMessage);
    }

    /**
     * Attempts to transform {@code originalApplication} using {@code transformationTemplate} and assert that transformation fails and is aborted,
     * besides resulting in the expected abort message.
     * It also returns a {@link TransformationResult} object, which can be used to assert in details this transformation generated
     * the expected result, metrics and abort details.
     * Notice that this transformation happens in a separated and temporary folder, keeping the original application folder preserved.
     *
     * @param facade Butterfly facade object, which is supposed to be auto-wired (using spring-test) into the calling class
     * @param originalApplication the original application, to be transformed
     * @param transformationTemplate the transformation template used to transform the original application
     * @param version the version the application should be upgraded to. This option only makes sense if the transformation template to be used is also an upgrade template.
     *                If not, it is ignored. If it is, but this option is not specified, then the application will be upgraded all the way to the latest version possible
     * @param properties a properties object specifying details about the transformation itself.
     *                   These properties help to specialize the
     *                   transformation, for example, determining if certain operations should
     *                   be skipped or not, or how certain aspects of the transformation should
     *                   be executed. The set of possible properties is defined by the used transformation
     *                   extension and template, read the documentation offered by the extension
     *                   for further details. The properties values are defined by the user requesting the transformation.
     *                   Properties are optional, so, if not desired, this parameter can be set to null.
     * @param expectedAbortMessage the expected abort message, to be compared against the actual
     * @return a {@link TransformationResult} object, which can be used to assert in details this transformation generated the expected result, metrics and abort details.
     */
    public static TransformationResult assertAbort(ButterflyFacade facade, File originalApplication, Class<? extends TransformationTemplate> transformationTemplate, String version, Properties properties, String expectedAbortMessage) {
        if (originalApplication == null || !originalApplication.exists() || !originalApplication.isDirectory()) {
            throw new IllegalArgumentException("Original application file is null, does not exist or is not a directory: " + (originalApplication == null ? "null" : originalApplication.getAbsolutePath()));
        }

        TransformationResult transformationResult = transform(facade, originalApplication, transformationTemplate, version, properties);

        if (!transformationResult.isSuccessful()) {
            String actualAbortMessage = transformationResult.getAbortDetails().getAbortMessage();
            if ((actualAbortMessage != null && expectedAbortMessage != null && !actualAbortMessage.equals(expectedAbortMessage)) ||
                    (actualAbortMessage == null && expectedAbortMessage != null) || (actualAbortMessage != null && expectedAbortMessage == null ) ) {
                throw new AssertionError("expected [" + expectedAbortMessage + "] but found [" + actualAbortMessage + "]");
            }
        } else {
            throw new AssertionError("Transformation did not abort, it completed successfully. Transformed application can be found at: " + transformationResult.getTransformedApplicationDir().getAbsolutePath());
        }

        return transformationResult;
    }

    /**
     * Check if given {@link TransformationResult} object has no warnings
     *
     * @param transformationResult the result object to be verified
     */
    public static void assertNoWarnings(TransformationResult transformationResult) {
        assertWarnings(transformationResult, 0);
    }

    /**
     * Check if given {@link TransformationResult} object has no errors
     *
     * @param transformationResult the result object to be verified
     */
    public static void assertNoErrors(TransformationResult transformationResult) {
        assertErrors(transformationResult, 0);
    }

    /**
     * Check if given {@link TransformationResult} object has {@code expectedNumberOfWarnings} number of warnings
     *
     * @param expectedNumberOfWarnings expected number of warnings generated during the transformation
     * @param transformationResult the result object to be verified
     */
    public static void assertWarnings(TransformationResult transformationResult, int expectedNumberOfWarnings) {
        long actualNumberOfWarnings = transformationResult.getMetrics().stream()
                .mapToInt(m -> m.getStatistics().getTUExecutionResultWarningCount() + m.getStatistics().getTOExecutionResultWarningCount()).sum();

        if (actualNumberOfWarnings != expectedNumberOfWarnings) {
            throw new AssertionError("expected [" + expectedNumberOfWarnings + "] but found [" + actualNumberOfWarnings + "]");
        }
    }

    /**
     * Check if given {@link TransformationResult} object has {@code expectedNumberOfErrors} number of errors
     *
     * @param expectedNumberOfErrors expected number of errors generated during the transformation
     * @param transformationResult the result object to be verified
     */
    public static void assertErrors(TransformationResult transformationResult, int expectedNumberOfErrors) {
        long actualNumberOfErrors = transformationResult.getMetrics().stream()
                .mapToInt(m -> m.getStatistics().getPerformResultErrorCount() + m.getStatistics().getTUExecutionResultErrorCount() + m.getStatistics().getTOExecutionResultErrorCount()).sum();

        if (actualNumberOfErrors != expectedNumberOfErrors) {
            throw new AssertionError("expected [" + expectedNumberOfErrors + "] but found [" + actualNumberOfErrors + "]");
        }
    }

}
