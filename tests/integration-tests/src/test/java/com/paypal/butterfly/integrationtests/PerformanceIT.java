package com.paypal.butterfly.integrationtests;

import com.google.common.base.Stopwatch;
import com.google.common.io.Files;
import com.paypal.butterfly.api.ButterflyFacade;
import com.paypal.butterfly.api.Configuration;
import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.extensions.springboot.JavaEEToSpringBoot;
import com.paypal.butterfly.test.ButterflyTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Simple performance tests to evaluate
 * functionality and parallel efficiency of
 * non-blocking asynchronous parallel execution
 * of transformations
 *
 * @author facarvalho
 */
@ContextConfiguration(classes = ButterflyTestConfig.class)
public class PerformanceIT extends AbstractTestNGSpringContextTests {

    private File sampleApp;
    private File sampleAppTransformedBaseline;
    private Properties properties;

    private static final int PARALLELISM = Runtime.getRuntime().availableProcessors();
    private static final int NUMBER_OF_PARALLEL_TRANSFORMATIONS = 200;
    private static final float MINIMUM_ACCEPTABLE_EFFICIENCY = 30f; // In percentage

    @Autowired
    private ButterflyFacade facade;

    @BeforeClass
    public void setUp() {
        sampleApp = new File("../sample-apps/echo");
        sampleAppTransformedBaseline = new File("../transformed-baseline/echo-JavaEEToSpringBoot");
        properties = new Properties();
        properties.put("changeReadme", "true");
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {

        // Perform one first transformation to warm system up
        Stopwatch warmUpStopwatch = Stopwatch.createStarted();
        transform().get();
        warmUpStopwatch.stop();

        // Execute one single transformation and take its execution time, to be used later as baseline
        Stopwatch singleStopwatch = Stopwatch.createStarted();
        TransformationResult transformationResult = transform().get();
        singleStopwatch.stop();

        // Make sure single transformation succeeded
        assertTrue(transformationResult.isSuccessful());
        com.paypal.butterfly.test.Assert.assertTransformation(sampleAppTransformedBaseline, transformationResult.getTransformedApplicationDir(), true);

        // Execute multiple transformations in parallel and time it
        CompletableFuture<TransformationResult>[] cfs = new CompletableFuture[NUMBER_OF_PARALLEL_TRANSFORMATIONS];
        Stopwatch parallelStopwatch = Stopwatch.createStarted();
        for (int i = 0; i < NUMBER_OF_PARALLEL_TRANSFORMATIONS; i++) cfs[i] = transform();

        // Wait until all parallel executions complete, then take its total duration
        CompletableFuture.allOf(cfs).join();
        parallelStopwatch.stop();

        // Make sure all parallel transformations succeeded (based on result metadata)
        assertTrue(Arrays.stream(cfs).map(cf -> {
            try {
                return cf.get().isSuccessful();
            } catch (Exception e) {
                return false;
            }
        }).allMatch(s -> s), "Parallel transformation failed");

        // Make sure all parallel transformations succeeded (based on baseline transformed application)
        Arrays.stream(cfs).forEach(cf -> {
            try {
                TransformationResult tr = cf.get();
                com.paypal.butterfly.test.Assert.assertTransformation(sampleAppTransformedBaseline, tr.getTransformedApplicationDir(), true);
            } catch (InterruptedException | ExecutionException e) {
                fail("Parallel transformation failed", e);
            }
        });

        long warmUpDuration = warmUpStopwatch.elapsed(TimeUnit.MILLISECONDS);
        long singleDuration = singleStopwatch.elapsed(TimeUnit.MILLISECONDS);
        long parallelDuration = parallelStopwatch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println("\n-------------------------------------------");
        System.out.printf("Warm-up execution duration:          %dms\n", warmUpDuration);
        System.out.printf("Single execution duration:           %dms\n", singleDuration);
        System.out.printf("Number of parallel transformations:  %d\n", NUMBER_OF_PARALLEL_TRANSFORMATIONS);
        System.out.printf("Number of available processors:      %d\n", PARALLELISM);
        System.out.printf("Parallel execution total duration:   %dms\n", parallelDuration);

        float parallelSpeedup = (singleDuration * NUMBER_OF_PARALLEL_TRANSFORMATIONS) / parallelDuration;
        float parallelEfficiency = 100 * parallelSpeedup / PARALLELISM;

        System.out.printf("Parallel speedup:                    %.2f\n", parallelSpeedup);
        System.out.printf("Parallel efficiency:                 %.2f%s\n", parallelEfficiency, "%");

        assertTrue(parallelEfficiency > MINIMUM_ACCEPTABLE_EFFICIENCY, "Parallel efficiency is too low.");
    }

    private CompletableFuture<TransformationResult> transform() {
        Configuration config = facade.newConfiguration(properties, Files.createTempDir(), false);
        return facade.transform(sampleApp, JavaEEToSpringBoot.class, null, config);
    }

}
