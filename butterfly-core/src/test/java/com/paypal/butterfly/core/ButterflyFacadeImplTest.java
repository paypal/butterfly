package com.paypal.butterfly.core;

import com.paypal.butterfly.api.Configuration;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.springboot.ButterflySpringBootExtension;
import com.paypal.butterfly.extensions.springboot.JavaEEToSpringBoot;
import com.paypal.butterfly.extensions.springboot.SpringBootUpgrade_1_5_6_to_1_5_7;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.*;

/**
 * ButterflyFacadeImplTest
 * <p>
 * Created by vkuncham on 11/7/2016.
 */
public class ButterflyFacadeImplTest extends PowerMockTestCase {

    @InjectMocks
    private ButterflyFacadeImpl butterflyFacadeImpl;

    @Mock
    private ExtensionRegistry extensionRegistry;

    @Mock
    private TransformationEngine transformationEngine;

    private ExtensionRegistry extensionRegistry_test = new ExtensionRegistry();

    private File applicationFolder = new File(this.getClass().getClassLoader().getResource("test-app-2").getFile());

    @Test
    public void testGetRegisteredExtension() {
        when(extensionRegistry.getExtensions()).thenReturn(extensionRegistry_test.getExtensions());
        List<Extension> extensions = butterflyFacadeImpl.getExtensions();
        assertNotNull(extensions);
        assertEquals(extensions.size(), 1);
        assertTrue(extensions.get(0) instanceof ButterflySpringBootExtension);
    }

    @Test
    public void testTransformWithValidTemplate() {
        butterflyFacadeImpl.transform(applicationFolder, JavaEEToSpringBoot.class);
        verify(transformationEngine, times(1)).perform((TemplateTransformationRequest) anyObject());
    }

    @Test(expectedExceptions = InternalException.class, expectedExceptionsMessageRegExp = "Template class class com.paypal.butterfly.extensions.api.TransformationTemplate could not be instantiated.*")
    public void testTransformWithAbstractTemplate() {
        butterflyFacadeImpl.transform(applicationFolder, TransformationTemplate.class);
    }

    @Test
    public void testTransformWithValidTemplateAsClass() {
        butterflyFacadeImpl.transform(applicationFolder, JavaEEToSpringBoot.class);
        verify(transformationEngine, times(1)).perform((TemplateTransformationRequest) anyObject());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Template class cannot be null")
    public void testTransformWithNullTemplateClass() {
        butterflyFacadeImpl.transform(applicationFolder, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Invalid application folder testTransformation1")
    public void testTransformWithValidUpgradePathInvalidAppFolder() {
        butterflyFacadeImpl.transform(new File("testTransformation1"), SpringBootUpgrade_1_5_6_to_1_5_7.class);
    }

    @Test
    public void testTransformWithValidUpgradePath() {
        butterflyFacadeImpl.transform(applicationFolder, SpringBootUpgrade_1_5_6_to_1_5_7.class);
        verify(transformationEngine, times(1)).perform((UpgradePathTransformationRequest) anyObject());
    }

    @Test
    public void newConfigurationTest() {
        assertNull(butterflyFacadeImpl.newConfiguration(null).getProperties());
        assertNull(butterflyFacadeImpl.newConfiguration(new Properties()).getProperties());

        try {
            Properties properties = new Properties();
            properties.put("", "v1");
            properties.put(" ", "v2");
            properties.put("a", new Object());
            properties.put("b%", "v4");
            butterflyFacadeImpl.newConfiguration(properties);
            fail("IllegalArgumentException was supposed to be thrown due to invalid properties");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "The following properties are invalid: [ , a, b%]");
        }

        Properties properties = new Properties();
        properties.put("a", "1");
        properties.put("b", "2");

        Configuration c1 = butterflyFacadeImpl.newConfiguration(properties);
        assertEquals(c1.getProperties(), properties);
        assertNull(c1.getOutputFolder());
        assertTrue(c1.isModifyOriginalFolder());
        assertFalse(c1.isZipOutput());

        Configuration c2 = butterflyFacadeImpl.newConfiguration(null, false);
        assertNull(c2.getProperties());
        assertNull(c2.getOutputFolder());
        assertFalse(c2.isModifyOriginalFolder());
        assertFalse(c2.isZipOutput());

        Configuration c3 = butterflyFacadeImpl.newConfiguration(null, true);
        assertNull(c3.getProperties());
        assertNull(c3.getOutputFolder());
        assertFalse(c3.isModifyOriginalFolder());
        assertTrue(c3.isZipOutput());

        File file = new File(".");

        Configuration c4 = butterflyFacadeImpl.newConfiguration(null, file, false);
        assertNull(c4.getProperties());
        assertEquals(c4.getOutputFolder(), file);
        assertFalse(c4.isModifyOriginalFolder());
        assertFalse(c4.isZipOutput());

        Configuration c5 = butterflyFacadeImpl.newConfiguration(null, file, true);
        assertNull(c5.getProperties());
        assertEquals(c5.getOutputFolder(), file);
        assertFalse(c5.isModifyOriginalFolder());
        assertTrue(c5.isZipOutput());
    }

}
