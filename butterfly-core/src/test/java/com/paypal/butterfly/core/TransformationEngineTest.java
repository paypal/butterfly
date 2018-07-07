package com.paypal.butterfly.core;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import com.paypal.butterfly.extensions.api.exception.ApplicationValidationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.io.Files;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.metrics.TransformationMetrics;
import com.paypal.butterfly.extensions.api.metrics.TransformationMetricsListener;
import com.paypal.butterfly.extensions.api.metrics.TransformationStatistics;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.extensions.springboot.ButterflySpringBootExtension;
import com.paypal.butterfly.extensions.springboot.JavaEEToSpringBoot;
import com.paypal.butterfly.extensions.springboot.SpringBootUpgrade_1_5_6_to_1_5_7;
import com.paypal.butterfly.facade.ButterflyProperties;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.TransformationResult;
import com.paypal.butterfly.facade.exception.TransformationException;

public class TransformationEngineTest extends TestHelper {

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private TransformationEngine transformationEngine;

    @InjectMocks
    private MetricsHandler metricsHandler;

    private SimpleTransformationMetricsListener metricsListener = new SimpleTransformationMetricsListener();

    @BeforeClass
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(applicationContext.getBeansOfType(TransformationListener.class)).thenReturn(Collections.singletonMap(null, metricsHandler));
        Mockito.when(applicationContext.getBeansOfType(TransformationMetricsListener.class)).thenReturn(Collections.singletonMap(null, metricsListener));
        Mockito.when(applicationContext.getBean(TransformationValidator.class)).thenReturn(new TransformationValidatorImpl());
        transformationEngine.setupListeners();
        metricsHandler.setupListeners();
    }

    private static class SimpleTransformationMetricsListener implements TransformationMetricsListener {
        List<TransformationMetrics> metricsList;
        @Override
        public void notify(List<TransformationMetrics> metricsList) {
            this.metricsList = metricsList;
        }
    }

    @Test
    public void basicTest() throws TransformationException {
        Application application = new Application(transformedAppFolder);
        Configuration configuration = new Configuration();

        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        transformationTemplate.add(getNewTestTransformationUtility());
        Transformation transformation = new TemplateTransformation(application, transformationTemplate, configuration);

        TransformationResult transformationResult = transformationEngine.perform(transformation);

        assertEquals(transformationResult.getConfiguration(), configuration);
        assertFalse(transformationResult.hasManualInstructions());
        assertNull(transformationResult.getManualInstructionsFile());
        assertEquals(transformationResult.getTransformedApplicationLocation(), transformedAppFolder);
        assertNotNull(transformationResult);

        List<TransformationMetrics> metricsList = metricsListener.metricsList;
        assertNotNull(metricsList);
        assertEquals(metricsList.size(), 1);

        TransformationMetrics metrics = metricsList.get(0);
        assertNull(metrics.getAbortDetails());
        assertNull(metrics.getApplicationName());
        assertNull(metrics.getApplicationType());
        assertEquals(metrics.getButterflyVersion(), ButterflyProperties.getString("butterfly.version"));
        assertNull(metrics.getFromVersion());
        assertNull(metrics.getUpgradeCorrelationId());
        assertNotNull(metrics.getMetricsId());
        assertEquals(metrics.getOriginalApplicationLocation(), transformedAppFolder.getAbsolutePath());
        assertEquals(metrics.getTemplateName(), transformationTemplate.getName());
        assertEquals(metrics.getUserId(), System.getProperty("user.name"));

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
    public void javaEEToSpringBootTest() throws TransformationException, IOException {
        File appFolder = new File("../tests/sample-apps/echo");
        File transformedAppFolder = new File("./out/test/resources/echo-transformed");
        FileUtils.deleteDirectory(transformedAppFolder);
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed sample app folder: %s\n", transformedAppFolder.getAbsolutePath());

        Application application = new Application(transformedAppFolder);
        Configuration configuration = new Configuration();

        TransformationTemplate transformationTemplate = new JavaEEToSpringBoot();
        Transformation transformation = new TemplateTransformation(application, transformationTemplate, configuration);

        TransformationResult transformationResult = transformationEngine.perform(transformation);

        assertEquals(transformationResult.getConfiguration(), configuration);
        assertFalse(transformationResult.hasManualInstructions());
        assertNull(transformationResult.getManualInstructionsFile());
        assertEquals(transformationResult.getTransformedApplicationLocation(), transformedAppFolder);
        assertNotNull(transformationResult);

        List<TransformationMetrics> metricsList = metricsListener.metricsList;
        assertNotNull(metricsList);
        assertEquals(metricsList.size(), 1);

        TransformationMetrics metrics = metricsList.get(0);
        assertNull(metrics.getAbortDetails());
        assertNull(metrics.getApplicationName());
        assertNull(metrics.getApplicationType());
        assertEquals(metrics.getButterflyVersion(), ButterflyProperties.getString("butterfly.version"));
        assertNull(metrics.getFromVersion());
        assertNull(metrics.getToVersion());
        assertNull(metrics.getUpgradeCorrelationId());
        assertNotNull(metrics.getMetricsId());
        assertEquals(metrics.getOriginalApplicationLocation(), transformedAppFolder.getAbsolutePath());
        assertEquals(metrics.getTemplateName(), transformationTemplate.getName());
        assertEquals(metrics.getUserId(), System.getProperty("user.name"));

        TransformationStatistics statistics = metrics.getStatistics();
        assertEquals(statistics.getManualInstructionsCount(), 0);
        assertEquals(statistics.getOperationsCount(), 16);
        assertEquals(statistics.getPerformResultErrorCount(), 0);
        assertEquals(statistics.getPerformResultExecutionResultCount(), 17);
        assertEquals(statistics.getPerformResultSkippedConditionCount(), 1);
        assertEquals(statistics.getPerformResultSkippedDependencyCount(), 0);
        assertEquals(statistics.getTOExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTOExecutionResultNoOpCount(), 0);
        assertEquals(statistics.getTOExecutionResultSuccessCount(), 16);
        assertEquals(statistics.getTOExecutionResultWarningCount(), 0);
        assertEquals(statistics.getUtilitiesCount(), 2);
        assertEquals(statistics.getTUExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTUExecutionResultNullCount(), 0);
        assertEquals(statistics.getTUExecutionResultValueCount(), 1);
        assertEquals(statistics.getTUExecutionResultWarningCount(), 0);
    }

    @Test(dependsOnMethods = "javaEEToSpringBootTest")
    public void springBootUpgradeTest() throws TransformationException {

        // The application to be modified is the result of javaEEToSpringBootTest
        File appFolder = new File("./out/test/resources/echo-transformed");

        Application application = new Application(appFolder);
        Configuration configuration = new Configuration();

        UpgradePath upgradePath = new UpgradePath(SpringBootUpgrade_1_5_6_to_1_5_7.class);
        Transformation transformation = new UpgradePathTransformation(application, upgradePath, configuration);

        TransformationResult transformationResult = transformationEngine.perform(transformation);

        assertEquals(transformationResult.getConfiguration(), configuration);
        assertFalse(transformationResult.hasManualInstructions());
        assertNull(transformationResult.getManualInstructionsFile());
        assertEquals(transformationResult.getTransformedApplicationLocation(), appFolder);
        assertNotNull(transformationResult);

        List<TransformationMetrics> metricsList = metricsListener.metricsList;
        assertNotNull(metricsList);
        assertEquals(metricsList.size(), 1);

        TransformationMetrics metrics = metricsList.get(0);
        assertNull(metrics.getAbortDetails());
        assertNull(metrics.getApplicationName());
        assertNull(metrics.getApplicationType());
        assertEquals(metrics.getButterflyVersion(), ButterflyProperties.getString("butterfly.version"));
        assertEquals(metrics.getFromVersion(), "1.5.6");
        assertEquals(metrics.getToVersion(), "1.5.7");
        assertFalse(StringUtils.isBlank(metrics.getUpgradeCorrelationId()));
        assertNotNull(metrics.getMetricsId());
        assertEquals(metrics.getOriginalApplicationLocation(), appFolder.getAbsolutePath());
        assertEquals(metrics.getTemplateName(), ButterflySpringBootExtension.class.getSimpleName() + ":" + SpringBootUpgrade_1_5_6_to_1_5_7.class.getSimpleName());
        assertEquals(System.getProperty("user.name"), metrics.getUserId());

        TransformationStatistics statistics = metrics.getStatistics();
        assertEquals(statistics.getManualInstructionsCount(), 0);
        assertEquals(statistics.getOperationsCount(), 1);
        assertEquals(statistics.getPerformResultErrorCount(), 0);
        assertEquals(statistics.getPerformResultExecutionResultCount(), 2);
        assertEquals(statistics.getPerformResultSkippedConditionCount(), 1);
        assertEquals(statistics.getPerformResultSkippedDependencyCount(), 0);
        assertEquals(statistics.getTOExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTOExecutionResultNoOpCount(), 0);
        assertEquals(statistics.getTOExecutionResultSuccessCount(), 1);
        assertEquals(statistics.getTOExecutionResultWarningCount(), 0);
        assertEquals(statistics.getUtilitiesCount(), 2);
        assertEquals(statistics.getTUExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTUExecutionResultNullCount(), 0);
        assertEquals(statistics.getTUExecutionResultValueCount(), 1);
        assertEquals(statistics.getTUExecutionResultWarningCount(), 0);
    }

    // TODO
//    @Test
    public void abortTest() throws TransformationException, IOException, URISyntaxException {
        File appFolder = new File(getClass().getResource("/test-app-2").toURI());

        File transformedAppFolder = Files.createTempDir();
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed sample app folder: %s\n", transformedAppFolder.getAbsolutePath());

        Application application = new Application(transformedAppFolder);
        Configuration configuration = new Configuration();

        TransformationTemplate transformationTemplate = new JavaEEToSpringBoot();
        Transformation transformation = new TemplateTransformation(application, transformationTemplate, configuration);

        TransformationResult transformationResult = transformationEngine.perform(transformation);

        assertEquals(transformationResult.getConfiguration(), configuration);
        assertFalse(transformationResult.hasManualInstructions());
        assertNull(transformationResult.getManualInstructionsFile());
        assertEquals(transformationResult.getTransformedApplicationLocation(), transformedAppFolder);
        assertNotNull(transformationResult);

        List<TransformationMetrics> metricsList = metricsListener.metricsList;
        assertNotNull(metricsList);
        assertEquals(metricsList.size(), 1);

        TransformationMetrics metrics = metricsList.get(0);
        assertNull(metrics.getAbortDetails());
        assertNull(metrics.getApplicationName());
        assertNull(metrics.getApplicationType());
        assertEquals(metrics.getButterflyVersion(), ButterflyProperties.getString("butterfly.version"));
        assertNull(metrics.getFromVersion());
        assertNull(metrics.getToVersion());
        assertNull(metrics.getUpgradeCorrelationId());
        assertNotNull(metrics.getMetricsId());
        assertEquals(metrics.getOriginalApplicationLocation(), transformedAppFolder.getAbsolutePath());
        assertEquals(metrics.getTemplateName(), transformationTemplate.getName());
        assertEquals(metrics.getUserId(), System.getProperty("user.name"));

        TransformationStatistics statistics = metrics.getStatistics();
        assertEquals(statistics.getManualInstructionsCount(), 0);
        assertEquals(statistics.getOperationsCount(), 0);
        assertEquals(statistics.getPerformResultErrorCount(), 0);
        assertEquals(statistics.getPerformResultExecutionResultCount(), 2);
        assertEquals(statistics.getPerformResultSkippedConditionCount(), 0);
        assertEquals(statistics.getPerformResultSkippedDependencyCount(), 0);
        assertEquals(statistics.getTOExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTOExecutionResultNoOpCount(), 0);
        assertEquals(statistics.getTOExecutionResultSuccessCount(), 0);
        assertEquals(statistics.getTOExecutionResultWarningCount(), 0);
        assertEquals(statistics.getUtilitiesCount(), 2);
        assertEquals(statistics.getTUExecutionResultErrorCount(), 0);
        assertEquals(statistics.getTUExecutionResultNullCount(), 0);
        assertEquals(statistics.getTUExecutionResultValueCount(), 1);
        assertEquals(statistics.getTUExecutionResultWarningCount(), 0);
    }

    @Test
    public void pendingManualChangesTest() throws IOException, URISyntaxException, TransformationException {
        File appFolder = new File(getClass().getResource("/test-app-3").toURI());

        File transformedAppFolder = Files.createTempDir();
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed sample app folder: %s\n", transformedAppFolder.getAbsolutePath());

        Application application = new Application(transformedAppFolder);
        Configuration configuration = new Configuration();

        TransformationTemplate transformationTemplate = new JavaEEToSpringBoot();
        Transformation transformation = new TemplateTransformation(application, transformationTemplate, configuration);

        try {
            transformationEngine.perform(transformation);
            fail("An ApplicationValidationException was supposed to be thrown (since application had a pending manual instruction) but was not");
        } catch (ApplicationValidationException e) {
            String exceptionMessage = String.format("This application has pending manual instructions. Perform manual instructions at the following file first, then remove it, and run Butterfly again: %s/%s", transformedAppFolder.getAbsolutePath(), MdFileManualInstructionsHandler.MANUAL_INSTRUCTIONS_MAIN_FILE);
            assertEquals(e.getMessage(), exceptionMessage);
        }
    }

}
