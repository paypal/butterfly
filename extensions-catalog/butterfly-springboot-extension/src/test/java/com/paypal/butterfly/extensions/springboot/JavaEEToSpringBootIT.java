package com.paypal.butterfly.extensions.springboot;

import java.io.File;

import org.testng.annotations.Test;

import com.paypal.butterfly.test.TransformationTest;

/**
 * Integration tests for {@link JavaEEToSpringBoot}
 *
 * @author facarvalho
 */
public class JavaEEToSpringBootIT extends TransformationTest {

    @Test
    public void sampleAppRunTest() {
        File sampleApp = new File("../../tests/sample-apps/echo");
        File sampleAppTransformedBaseline = new File("../../tests/transformed-baseline/echo-JavaEEToSpringBoot");

        assertTransformation(sampleAppTransformedBaseline, sampleApp, JavaEEToSpringBoot.class);
    }

}
