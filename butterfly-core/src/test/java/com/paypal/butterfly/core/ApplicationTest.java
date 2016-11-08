package com.paypal.butterfly.core;

import org.mockito.InjectMocks;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * ApplicationTest
 *
 * Created by vkuncham on 11/1/2016.
 */
public class ApplicationTest extends PowerMockTestCase {

    @InjectMocks
    private Application application = new Application(new File(System.getProperty("user.dir")));

    @Test
    public void testGetFolder() {
        Assert.assertTrue(System.getProperty("user.dir").equals(application.getFolder().getAbsolutePath()));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFolderNotExists() {
        application.setFolder(new File(System.getProperty("user.dir")+"\\test_transformed"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFolderAsNone() {
        application.setFolder(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFolderIsNotDirectory() {
        application.setFolder(new File(System.getProperty("user.dir")+"\\test_transformed.zip"));
    }


}
