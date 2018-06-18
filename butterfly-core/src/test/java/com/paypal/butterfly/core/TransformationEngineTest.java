package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.metrics.TransformationMetrics;
import com.paypal.butterfly.extensions.api.metrics.TransformationMetricsListener;
import com.paypal.butterfly.extensions.api.metrics.TransformationStatistics;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.TransformationResult;
import com.paypal.butterfly.facade.exception.TransformationException;
import org.apache.commons.io.FileUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertNull;

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
        transformationEngine.setupListeners();
        metricsHandler.setupListeners();
    }

    static class SimpleTransformationMetricsListener implements TransformationMetricsListener {
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
        assertNull(metrics.getButterflyVersion());
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
    public void sampleExtensionTest() throws TransformationException, IOException {
        File appFolder = new File("../tests/sample-app");
        File transformedAppFolder = new File("./out/test/resources/sample-app-transformed");
        FileUtils.deleteDirectory(transformedAppFolder);
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed sample app folder: %s\n", transformedAppFolder.getAbsolutePath());

        Application application = new Application(transformedAppFolder);
        Configuration configuration = new Configuration();

        TransformationTemplate transformationTemplate = new com.extensiontest.SampleTransformationTemplate();
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
        assertNull(metrics.getButterflyVersion());
        assertNull(metrics.getFromVersion());
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

}
