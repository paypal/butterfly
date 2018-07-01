package com.paypal.butterfly.extensions.springboot;

import static com.paypal.butterfly.test.Assert.assertTransformation;

import java.io.File;

import org.testng.annotations.Test;

/**
 * Integration tests for {@link JavaEEToSpringBoot}
 *
 * @author facarvalho
 */
public class JavaEEToSpringBootIT {

    @Test
    public void sampleAppRunTest() {
        File sampleApp = new File("../../tests/sample-apps/echo");
        File sampleAppTransformedBaseline = new File("../../tests/transformed-baseline/echo-JavaEEToSpringBoot");

        assertTransformation(sampleAppTransformedBaseline, sampleApp, JavaEEToSpringBoot.class);
    }

}
