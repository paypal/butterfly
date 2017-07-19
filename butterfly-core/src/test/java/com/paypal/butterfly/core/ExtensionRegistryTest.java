package com.paypal.butterfly.core;

import com.paypal.butterfly.core.sample.ExtensionSample;
import com.paypal.butterfly.extensions.api.Extension;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * ExtensionRegistry Test
 *
 * Created by vkuncham on 11/1/2016.
 */
public class ExtensionRegistryTest {

    @Test
    public void testValidExtensionRegistry() {
        ExtensionRegistry extensionRegistry  = new ExtensionRegistry();
        Extension extension = extensionRegistry.getExtension();
        Assert.assertNotNull(extension);
        Assert.assertTrue(extension instanceof ExtensionSample);
    }

}
