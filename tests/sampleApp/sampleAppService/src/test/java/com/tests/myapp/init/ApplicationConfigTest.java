package com.tests.myapp.init;

import org.junit.Assert;
import org.junit.Test;

import com.tests.myapp.init.ApplicationConfig;

import javax.ws.rs.ApplicationPath;

/**
 *  Unit Test for {@link ApplicationConfig}.
 */

public class ApplicationConfigTest {
    private static final String APPLICATION_PATH = "/v1/sampleapp/";
    
    @Test
    public void testApplicationPath() throws ClassNotFoundException {
        ApplicationConfig c = new ApplicationConfig();
        String result = (c.getClass().getAnnotation(ApplicationPath.class)).value();
        Assert.assertEquals(APPLICATION_PATH, result);
    }

}
