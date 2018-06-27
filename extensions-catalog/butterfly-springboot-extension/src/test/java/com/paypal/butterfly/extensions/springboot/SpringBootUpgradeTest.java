package com.paypal.butterfly.extensions.springboot;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.testng.annotations.Test;

import com.paypal.butterfly.extensions.api.TransformationUtility;

/**
 * Unit tests for upgrade templates
 *
 * @author facarvalho
 */
public class SpringBootUpgradeTest {

    @Test
    public void test() {
        SpringBootUpgrade_1_5_6_to_1_5_7 us_1_5_6_to_1_5_7 = new SpringBootUpgrade_1_5_6_to_1_5_7();

        assertEquals(us_1_5_6_to_1_5_7.getExtensionClass(), ButterflySpringBootExtension.class);
        assertEquals(us_1_5_6_to_1_5_7.getDescription(), "Upgrade Spring Boot application from version 1.5.6 to version 1.5.7");
        assertEquals(us_1_5_6_to_1_5_7.getName(), "ButterflySpringBootExtension:SpringBootUpgrade_1_5_6_to_1_5_7");

        List<TransformationUtility> utilities = us_1_5_6_to_1_5_7.getUtilities();

        assertNotNull(utilities);
        assertEquals(us_1_5_6_to_1_5_7.getChildren(), utilities);
        assertEquals(utilities.size(), 3);
    }

}
