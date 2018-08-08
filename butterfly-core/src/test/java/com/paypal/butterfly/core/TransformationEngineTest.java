package com.paypal.butterfly.core;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.io.Files;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ApplicationValidationException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.extensions.api.utilities.Abort;
import com.paypal.butterfly.extensions.springboot.ButterflySpringBootExtension;
import com.paypal.butterfly.extensions.springboot.JavaEEToSpringBoot;
import com.paypal.butterfly.extensions.springboot.SpringBootUpgrade_1_5_6_to_1_5_7;
import com.paypal.butterfly.api.Application;
import com.paypal.butterfly.api.Configuration;
import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.api.AbortDetails;
import com.paypal.butterfly.api.TransformationMetrics;
import com.paypal.butterfly.api.TransformationStatistics;

public class TransformationEngineTest extends TestHelper {

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private TransformationEngine transformationEngine;

    @BeforeClass
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(applicationContext.getBean(TransformationValidator.class)).thenReturn(new TransformationValidatorImpl());
        Mockito.when(applicationContext.getBean(ManualInstructionsHandler.class)).thenReturn(new ManualInstructionsHandler());
        transformationEngine.setupListeners();
    }

    @Test
    public void basicTest() {
        Application application = new ApplicationImpl(transformedAppFolder);
        Configuration configuration = new ConfigurationImpl();

        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        transformationTemplate.add(getNewTestTransformationUtility());
        AbstractTransformationRequest transformation = new TemplateTransformationRequest(application, transformationTemplate, configuration);

        TransformationResult transformationResult = transformationEngine.perform(transformation);

        assertNotNull(transformationResult);
        assertTrue(transformationResult.isSuccessful());
        assertEquals(transformationResult.getTransformationRequest().getConfiguration(), configuration);
        assertFalse(transformationResult.hasManualInstructions());
        assertNull(transformationResult.getManualInstructionsFile());
        assertEquals(transformationResult.getTransformedApplicationDir(), transformedAppFolder);
        assertNull(transformationResult.getAbortDetails());

        List<TransformationMetrics> metricsList = transformationResult.getMetrics();
        assertNotNull(metricsList);
        assertEquals(metricsList.size(), 1);

        TransformationMetrics metrics = metricsList.get(0);
        assertNull(metrics.getFromVersion());

        TransformationStatistics statistics = metrics.getStatistics();
        assertEquals(statistics.getManualInstructionsCount(), 0);
        assertEquals(statistics.getOperationsCount(), 0);
        assertEquals(statistics.getPerformResultErrorCount(), 0);
        assertEquals(statistics.getPerformResultExecutionResultCount(), 1);
        assertEquals(statistics.getPerformResultSkippedConditionCount(), 0);
        assertEquals(statistics.getPerformResultSkippedDependencyCount(), 0);
        assertEquals(statistics.getTOExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTOExecutionResultNoOpCount(), 0);
        assertEquals(statistics.getTOExecutionResultSuccessCount(), 0);
        assertEquals(statistics.getTOExecutionResultWarningCount(), 0);
        assertEquals(statistics.getUtilitiesCount(), 1);
        assertEquals(statistics.getTUExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTUExecutionResultNullCount(), 0);
        assertEquals(statistics.getTUExecutionResultValueCount(), 1);
        assertEquals(statistics.getTUExecutionResultWarningCount(), 0);
    }

    @Test
    public void javaEEToSpringBootTest() throws IOException {
        File appFolder = new File("../tests/sample-apps/echo");
        File transformedAppFolder = new File("./out/test/resources/echo-transformed");
        FileUtils.deleteDirectory(transformedAppFolder);
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed sample app folder: %s\n", transformedAppFolder.getAbsolutePath());

        Application application = new ApplicationImpl(transformedAppFolder);
        Configuration configuration = new ConfigurationImpl();

        TransformationTemplate transformationTemplate = new JavaEEToSpringBoot();
        AbstractTransformationRequest transformation = new TemplateTransformationRequest(application, transformationTemplate, configuration);

        TransformationResult transformationResult = transformationEngine.perform(transformation);

        assertNotNull(transformationResult);
        assertTrue(transformationResult.isSuccessful());
        assertEquals(transformationResult.getTransformationRequest().getConfiguration(), configuration);
        assertFalse(transformationResult.hasManualInstructions());
        assertNull(transformationResult.getManualInstructionsFile());
        assertEquals(transformationResult.getTransformedApplicationDir(), transformedAppFolder);
        assertNull(transformationResult.getAbortDetails());

        List<TransformationMetrics> metricsList = transformationResult.getMetrics();
        assertNotNull(metricsList);
        assertEquals(metricsList.size(), 1);

        TransformationMetrics metrics = metricsList.get(0);
        assertNull(metrics.getFromVersion());
        assertNull(metrics.getToVersion());

        TransformationStatistics statistics = metrics.getStatistics();
        assertEquals(statistics.getPerformResultErrorCount(), 0);
        assertEquals(statistics.getPerformResultSkippedConditionCount(), 1);
        assertEquals(statistics.getPerformResultSkippedDependencyCount(), 0);
        assertEquals(statistics.getTUExecutionResultWarningCount(), 0);
        assertEquals(statistics.getTUExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTOExecutionResultWarningCount(), 0);
        assertEquals(statistics.getTOExecutionResultErrorCount(), 0);
        assertEquals(statistics.getManualInstructionsCount(), 0);
    }

    @Test(dependsOnMethods = "javaEEToSpringBootTest")
    public void springBootUpgradeTest() {

        // The application to be modified is the result of javaEEToSpringBootTest
        File appFolder = new File("./out/test/resources/echo-transformed");

        Application application = new ApplicationImpl(appFolder);
        Configuration configuration = new ConfigurationImpl();

        UpgradePath upgradePath = new UpgradePath(SpringBootUpgrade_1_5_6_to_1_5_7.class);
        AbstractTransformationRequest transformation = new UpgradePathTransformationRequest(application, upgradePath, configuration);

        TransformationResult transformationResult = transformationEngine.perform(transformation);

        assertNotNull(transformationResult);
        assertTrue(transformationResult.isSuccessful());
        assertEquals(transformationResult.getTransformationRequest().getConfiguration(), configuration);
        assertFalse(transformationResult.hasManualInstructions());
        assertNull(transformationResult.getManualInstructionsFile());
        assertEquals(transformationResult.getTransformedApplicationDir(), appFolder);
        assertNull(transformationResult.getAbortDetails());

        List<TransformationMetrics> metricsList = transformationResult.getMetrics();
        assertNotNull(metricsList);
        assertEquals(metricsList.size(), 1);

        TransformationMetrics metrics = metricsList.get(0);
        assertEquals(metrics.getFromVersion(), "1.5.6");
        assertEquals(metrics.getToVersion(), "1.5.7");

        TransformationStatistics statistics = metrics.getStatistics();
        assertEquals(statistics.getManualInstructionsCount(), 0);
        assertEquals(statistics.getPerformResultErrorCount(), 0);
        assertEquals(statistics.getPerformResultSkippedConditionCount(), 1);
        assertEquals(statistics.getPerformResultSkippedDependencyCount(), 0);
        assertEquals(statistics.getTOExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTOExecutionResultWarningCount(), 0);
        assertEquals(statistics.getTUExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTUExecutionResultWarningCount(), 0);
    }

    @Test
    public void abortTest() throws IOException, URISyntaxException {
        File appFolder = new File(getClass().getResource("/test-app-2").toURI());

        File transformedAppFolder = Files.createTempDir();
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed sample app folder: %s\n", transformedAppFolder.getAbsolutePath());

        Application application = new ApplicationImpl(transformedAppFolder);
        Configuration configuration = new ConfigurationImpl();

        TransformationTemplate transformationTemplate = new JavaEEToSpringBoot();
        AbstractTransformationRequest transformation = new TemplateTransformationRequest(application, transformationTemplate, configuration);

        TransformationResult transformationResult = transformationEngine.perform(transformation);

        assertNotNull(transformationResult);
        assertFalse(transformationResult.isSuccessful());
        assertEquals(transformationResult.getTransformationRequest().getConfiguration(), configuration);
        assertFalse(transformationResult.hasManualInstructions());
        assertNull(transformationResult.getManualInstructionsFile());
        assertEquals(transformationResult.getTransformedApplicationDir(), transformedAppFolder);
        assertNotNull(transformationResult.getAbortDetails());

        List<TransformationMetrics> metricsList = transformationResult.getMetrics();
        assertNotNull(metricsList);
        assertEquals(metricsList.size(), 1);

        TransformationMetrics metrics = metricsList.get(0);
        assertNull(metrics.getFromVersion());
        assertNull(metrics.getToVersion());

        TransformationStatistics statistics = metrics.getStatistics();
        assertEquals(statistics.getTUExecutionResultValueCount(), 1);
        assertEquals(statistics.getManualInstructionsCount(), 0);
        assertEquals(statistics.getPerformResultErrorCount(), 0);
        assertEquals(statistics.getPerformResultSkippedConditionCount(), 0);
        assertEquals(statistics.getPerformResultSkippedDependencyCount(), 0);
        assertEquals(statistics.getTOExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTOExecutionResultWarningCount(), 0);
        assertEquals(statistics.getTUExecutionResultErrorCount(), 1);
        assertEquals(statistics.getTUExecutionResultWarningCount(), 0);

        AbortDetails abortDetails = transformationResult.getAbortDetails();

        assertEquals(abortDetails.getTemplateName(), ButterflySpringBootExtension.class.getSimpleName() + ":" + JavaEEToSpringBoot.class.getSimpleName());
        assertEquals(abortDetails.getTemplateClassName(), JavaEEToSpringBoot.class.getName());
        assertEquals(abortDetails.getUtilityName(), "ButterflySpringBootExtension:JavaEEToSpringBoot-2-Abort");
        assertEquals(abortDetails.getUtilityClassName(), Abort.class.getName());
        assertEquals(abortDetails.getAbortMessage(), "This application does not have a root pom.xml file");
        assertEquals(abortDetails.getExceptionClassName(), TransformationUtilityException.class.getName());
        assertEquals(abortDetails.getExceptionMessage(), "Abort transformation utility has been executed");
        assertNotNull(abortDetails.getExceptionStackTrace());
        assertTrue(abortDetails.getExceptionStackTrace().startsWith(TransformationUtilityException.class.getName() + ": Abort transformation utility has been executed\n\tat " + Abort.class.getName() + ".execution("));
    }

    @Test
    public void pendingManualChangesTest() throws IOException, URISyntaxException {
        File appFolder = new File(getClass().getResource("/test-app-3").toURI());

        File transformedAppFolder = Files.createTempDir();
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed sample app folder: %s\n", transformedAppFolder.getAbsolutePath());

        Application application = new ApplicationImpl(transformedAppFolder);
        Configuration configuration = new ConfigurationImpl();

        TransformationTemplate transformationTemplate = new JavaEEToSpringBoot();
        AbstractTransformationRequest transformation = new TemplateTransformationRequest(application, transformationTemplate, configuration);

        try {
            transformationEngine.perform(transformation);
            fail("An ApplicationValidationException was supposed to be thrown (since application had a pending manual instruction) but was not");
        } catch (ApplicationValidationException e) {
            String exceptionMessage = String.format("This application has pending manual instructions. Perform manual instructions at the following file first, then remove it, and run Butterfly again: %s/%s", transformedAppFolder.getAbsolutePath(), ManualInstructionsHandler.MANUAL_INSTRUCTIONS_MAIN_FILE);
            assertEquals(e.getMessage(), exceptionMessage);
        }
    }

}
