package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.springboot.ButterflySpringBootExtension;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * ExtensionRegistry Test
 *
 * Created by vkuncham on 11/1/2016.
 */
public class ExtensionRegistryTest {

    @Test
    public void testValidExtensionRegistry() {
        ExtensionRegistry extensionRegistry  = new ExtensionRegistry();
        List<Extension> extensions = extensionRegistry.getExtensions();
        Assert.assertNotNull(extensions);
        Assert.assertEquals(extensions.size(), 1);
        Assert.assertTrue(extensions.get(0) instanceof ButterflySpringBootExtension);
    }

}
