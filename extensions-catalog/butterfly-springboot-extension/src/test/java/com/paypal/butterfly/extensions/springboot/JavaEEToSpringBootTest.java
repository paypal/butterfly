package com.paypal.butterfly.extensions.springboot;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.testng.annotations.Test;

import com.paypal.butterfly.extensions.api.TransformationUtility;

/**
 * Unit tests for {@link JavaEEToSpringBoot}
 *
 * @author facarvalho
 */
public class JavaEEToSpringBootTest {

    @Test
    public void test() {
        JavaEEToSpringBoot javaEEToSpringBoot = new JavaEEToSpringBoot();

        assertEquals(javaEEToSpringBoot.getExtensionClass(), ButterflySpringBootExtension.class);
        assertEquals(javaEEToSpringBoot.getDescription(), "Java EE to Spring Boot transformation template");
        assertEquals(javaEEToSpringBoot.getName(), "ButterflySpringBootExtension:JavaEEToSpringBoot");

        List<TransformationUtility> utilities = javaEEToSpringBoot.getUtilities();

        assertNotNull(utilities);
        assertEquals(javaEEToSpringBoot.getChildren(), utilities);
        assertEquals(utilities.size(), 18);
    }

}
