package com.paypal.butterfly.integrationtests;

import static com.paypal.butterfly.test.Assert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.testng.annotations.Test;

import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.extensions.springboot.JavaEEToSpringBoot;

public class StaticRemovalIT {

    @Test
    public void test1() {
        test();
    }

    @Test
    public void test2() {
        test();
    }

    @Test
    public void test3() {
        test();
    }

    @Test
    public void test4() {
        test();
    }

    public void test() {
        File sampleApp = new File("../sample-apps/echo");
        File sampleAppTransformedBaseline = new File("../transformed-baseline/echo-JavaEEToSpringBoot");

        TransformationResult transformationResult = assertTransformation(sampleAppTransformedBaseline, sampleApp, JavaEEToSpringBoot.class, true, false, null, true);

        assertTrue(transformationResult.isSuccessful());
        assertNoWarnings(transformationResult);
        assertNoErrors(transformationResult);
        assertEquals(transformationResult.getManualInstructionsTotal(), 0);
    }

}
