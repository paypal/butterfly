package com.tests.myapp.impl;

import junit.framework.Assert;
import org.junit.Test;

import com.tests.myapp.api.SampleResource;
import com.tests.myapp.impl.SampleResourceImpl;

/**
 *  Unit Test for {@link SampleResourceImpl}.
 */

public class SampleResourceImplTest {
    private static final String HELLO_WORLD = "Hello, World '";

    @Test
    public void testSayHello() {
        SampleResource sampleResource = new SampleResourceImpl();
        String result = sampleResource.sayHello();
        Assert.assertEquals(0,result.indexOf(HELLO_WORLD));
    }

}
