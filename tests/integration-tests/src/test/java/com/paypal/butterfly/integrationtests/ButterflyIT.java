package com.paypal.butterfly.integrationtests;

import com.paypal.butterfly.api.ButterflyFacade;
import com.paypal.butterfly.extensions.api.exception.ApplicationValidationException;
import com.paypal.butterfly.extensions.springboot.JavaEEToSpringBoot;
import com.paypal.butterfly.test.ButterflyTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.paypal.butterfly.test.Assert.assertTransformation;

@ContextConfiguration(classes = ButterflyTestConfig.class)
public class ButterflyIT extends AbstractTestNGSpringContextTests {

    private File sampleApp;
    private File sampleAppTransformedBaseline;

    @Autowired
    private ButterflyFacade facade;

    @BeforeClass
    public void setUp() {
        sampleApp = new File("../sample-apps/echo");
        sampleAppTransformedBaseline = new File("../transformed-baseline/echo-JavaEEToSpringBoot");
    }

    @Test
    public void sampleAppRunTest() {
        Properties properties = new Properties();
        properties.put("changeReadme", "true");

        assertTransformation(facade, sampleAppTransformedBaseline, sampleApp, JavaEEToSpringBoot.class, null, properties, true);
    }

    // FIXME Probably it would be better if this exception is taken care of by Butterfly core (and packaged to the transformation result object), as opposed to carry forward this exception
    @Test(expectedExceptions = {AssertionError.class}, expectedExceptionsMessageRegExp = "java\\.util\\.concurrent\\.ExecutionException: com\\.paypal\\.butterfly\\.extensions\\.api\\.exception\\.ApplicationValidationException: This application has pending manual instructions\\. Perform manual instructions at the following file first, then remove it, and run Butterfly again: (.*)/BUTTERFLY_MANUAL_INSTRUCTIONS\\.md")
    public void pendingManualChangesTest() throws URISyntaxException {
        File appDir = new File(this.getClass().getResource("/test-app-1").toURI());
        assertTransformation(facade, sampleAppTransformedBaseline, appDir, JavaEEToSpringBoot.class, null, null, true);
    }

}