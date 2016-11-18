package com.paypal.butterfly.core;

import com.paypal.butterfly.core.sample.ExtensionSampleOne;
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
    public void testValidExtensionRegistry() {
        ExtensionRegistry extensionRegistry  = new ExtensionRegistry();
        List<Extension> extensions = extensionRegistry.getExtensions();
        Assert.assertEquals(extensions.size(),2);
        Assert.assertTrue(extensions.get(0) instanceof ExtensionSampleOne);
        Assert.assertTrue(extensions.get(1) instanceof ExtensionSampleOne);
    }

}
