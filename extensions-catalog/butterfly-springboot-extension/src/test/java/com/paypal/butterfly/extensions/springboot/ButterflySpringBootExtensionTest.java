package com.paypal.butterfly.extensions.springboot;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit tests for {@link ButterflySpringBootExtension}
 *
 * @author facarvalho
 */
public class ButterflySpringBootExtensionTest {

    @Test
    public void test() {
        ButterflySpringBootExtension butterflySpringBootExtension = new ButterflySpringBootExtension();

        assertEquals(butterflySpringBootExtension.getDescription(), "Butterfly Spring Boot extension");
        assertEquals(butterflySpringBootExtension.getVersion(), "1.0.0");
        assertNull(butterflySpringBootExtension.automaticResolution(null));
        assertNotNull(butterflySpringBootExtension.getTemplateClasses());
        assertEquals(butterflySpringBootExtension.getTemplateClasses().size(), 2);
        assertTrue(butterflySpringBootExtension.getTemplateClasses().contains(JavaEEToSpringBoot.class));
        assertTrue(butterflySpringBootExtension.getTemplateClasses().contains(SpringBootUpgrade_1_5_6_to_1_5_7.class));
    }

}
