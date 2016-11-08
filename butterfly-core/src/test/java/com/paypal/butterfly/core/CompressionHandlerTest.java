package com.paypal.butterfly.core;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * CompressionHandlerTest
 *
 * Created by vkuncham on 10/31/2016.
 */
public class CompressionHandlerTest extends PowerMockTestCase {

    @InjectMocks
    private CompressionHandler compressionHandler;

    @Mock
    private Transformation transformation;
    private File transformedApplicationLocation;

    @Test
    public void testCompressionWithValidFilePath() {

        Assert.assertNotNull(compressionHandler);
        Assert.assertNotNull(transformation);
        try {
            transformedApplicationLocation = new File(System.getProperty("user.dir") + "\\test_transformed");
            boolean created = transformedApplicationLocation.mkdirs();
            when(transformation.getTransformedApplicationLocation()).thenReturn(transformedApplicationLocation);
            compressionHandler.compress(transformation);
            if(created) {
                Assert.assertTrue(new File(transformation.getTransformedApplicationLocation().getAbsolutePath() + ".zip").exists());
                Assert.assertTrue(!new File(transformation.getTransformedApplicationLocation().getAbsolutePath()).exists());
            } else {
                Assert.assertFalse(new File(transformation.getTransformedApplicationLocation().getAbsolutePath() + ".zip").exists());
                Assert.assertFalse(new File(transformation.getTransformedApplicationLocation().getAbsolutePath()).exists());
            }
        } catch(Exception ex) {
            Assert.assertFalse(false);
            ex.printStackTrace();
        }
    }

    @Test
    public void testCompressionWithInValidFilePath() {
        Assert.assertNotNull(compressionHandler);
        Assert.assertNotNull(transformation);
        transformedApplicationLocation = new File(System.getProperty("user.dir") + "\\test1_transformed");
        when(transformation.getTransformedApplicationLocation()).thenReturn(transformedApplicationLocation);
        compressionHandler.compress(transformation);
        Assert.assertFalse(new File(transformation.getTransformedApplicationLocation().getAbsolutePath() + ".zip").exists());
        Assert.assertFalse(new File(transformation.getTransformedApplicationLocation().getAbsolutePath()).exists());
    }

    @AfterMethod
    public void tearDown(){
        try {
            File compressedFile = new File(transformation.getTransformedApplicationLocation().getAbsolutePath() + ".zip");
            if(compressedFile.exists()) {
                boolean deleted = compressedFile.delete();
                System.out.println("In tearDown : Removed Compressed File :" + deleted);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
