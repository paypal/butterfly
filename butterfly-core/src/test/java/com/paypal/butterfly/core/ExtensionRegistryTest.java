package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.Extension;
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
    public void testValidExtensionRegistry_1() {
        ExtensionRegistry extensionRegistry  = new ExtensionRegistry();
        List<Extension> extensions = extensionRegistry.getExtensions();
        Assert.assertTrue(extensions.size() == 2);
        Extension extension = extensions.get(0);
        Assert.assertTrue(extension instanceof ExtensionSampleOne);
    }


    @Test
    public void testValidExtensionRegistry_2() {
        ExtensionRegistry extensionRegistry  = new ExtensionRegistry();
        List<Extension> extensions = extensionRegistry.getExtensions();
        Assert.assertTrue(extensions.size() == 2);
        Extension extension = extensions.get(1);
        Assert.assertTrue(extension instanceof ExtensionSampleOne);
    }

}
