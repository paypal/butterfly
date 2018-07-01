package com.paypal.butterfly.extensions.springboot;

import static com.paypal.butterfly.test.Assert.assertTransformation;

import java.io.File;

import org.testng.annotations.Test;

/**
 * Integration tests for upgrade steps
 *
 * @author facarvalho
 */
public class SpringBootUpgradeIT {

    @Test
    public void test() {
        File sampleApp = new File("../../tests/transformed-baseline/echo-JavaEEToSpringBoot");
        File sampleAppTransformedBaseline = new File("../../tests/transformed-baseline/echo-SpringBoot-1.5.7");

        assertTransformation(sampleAppTransformedBaseline, sampleApp, SpringBootUpgrade_1_5_6_to_1_5_7.class);
    }

}
