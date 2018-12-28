package com.paypal.butterfly.extensions.springboot;

import com.paypal.butterfly.api.ButterflyFacade;
import com.paypal.butterfly.api.TransformationMetrics;
import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.api.TransformationStatistics;
import com.paypal.butterfly.test.ButterflyTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static com.paypal.butterfly.test.Assert.*;
import static org.testng.Assert.*;

/**
 * Integration tests for {@link JavaEEToSpringBoot}
 *
 * @author facarvalho
 */
@ContextConfiguration(classes = ButterflyTestConfig.class)
public class JavaEEToSpringBootIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private ButterflyFacade facade;

    @BeforeClass
    public void setDebug() {
        System.setProperty("butterfly.test.debug", "true");
    }

    @Test
    public void sampleAppRunTest() {
        File sampleApp = new File("../../tests/sample-apps/echo");
        File sampleAppTransformedBaseline = new File("../../tests/transformed-baseline/echo-JavaEEToSpringBoot");

        TransformationResult transformationResult = assertTransformation(facade, sampleAppTransformedBaseline, sampleApp, JavaEEToSpringBoot.class, null, true);

        assertTrue(transformationResult.isSuccessful());
// FIXME
//        assertEquals(transformationResult.getApplicationType(), "REST");
//        assertEquals(transformationResult.getApplicationName(), "echo");
        assertFalse(transformationResult.hasManualInstructions());
        assertEquals(transformationResult.getManualInstructionsTotal(), 0);

        assertNoWarnings(transformationResult);
        assertNoErrors(transformationResult);

        List<TransformationMetrics> metrics = transformationResult.getMetrics();

        assertNotNull(metrics);
        assertEquals(metrics.size(), 1);

        TransformationMetrics transformationMetrics = metrics.get(0);

        assertNull(transformationMetrics.getFromVersion());
        assertNull(transformationMetrics.getToVersion());
        assertEquals(transformationMetrics.getTemplateName(),  ButterflySpringBootExtension.class.getSimpleName() + ":" + JavaEEToSpringBoot.class.getSimpleName());
        assertEquals(transformationMetrics.getTemplateClassName(), JavaEEToSpringBoot.class.getName());
        assertTrue(transformationMetrics.isSuccessful());
        assertFalse(transformationMetrics.hasManualInstructions());

        TransformationStatistics statistics = transformationMetrics.getStatistics();

        assertEquals(statistics.getPerformResultSkippedConditionCount(), 1);
        assertEquals(statistics.getPerformResultSkippedDependencyCount(), 0);
        assertEquals(statistics.getManualInstructionsCount(), 0);
    }

}
