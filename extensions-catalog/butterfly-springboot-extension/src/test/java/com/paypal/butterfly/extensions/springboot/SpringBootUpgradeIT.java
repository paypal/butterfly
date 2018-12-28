package com.paypal.butterfly.extensions.springboot;

import com.paypal.butterfly.api.ButterflyFacade;
import com.paypal.butterfly.test.ButterflyTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.File;

import static com.paypal.butterfly.test.Assert.assertTransformation;

/**
 * Integration tests for upgrade steps
 *
 * @author facarvalho
 */
@ContextConfiguration(classes = ButterflyTestConfig.class)
public class SpringBootUpgradeIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private ButterflyFacade facade;

    @Test
    public void test() {
        File sampleApp = new File("../../tests/transformed-baseline/echo-JavaEEToSpringBoot");
        File sampleAppTransformedBaseline = new File("../../tests/transformed-baseline/echo-SpringBoot-1.5.7");

        assertTransformation(facade, sampleAppTransformedBaseline, sampleApp, SpringBootUpgrade_1_5_6_to_1_5_7.class, null, true);
    }

}
