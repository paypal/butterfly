package com.paypal.butterfly.core;

import org.apache.commons.io.FileUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * CompressionHandlerTest
 *
 * Created by vkuncham on 10/31/2016.
 */

@PrepareForTest(FileUtils.class)
public class CompressionHandlerTest extends PowerMockTestCase {

    private static final Logger logger = LoggerFactory.getLogger(CompressionHandlerTest.class);

    @InjectMocks
    private CompressionHandler compressionHandler;

    @Mock
    private Transformation transformation;

    private File transformedApplicationLocation;

    @Test
    public void testCompressionWithValidFilePath() throws IOException {
        Assert.assertNotNull(transformation);
        transformedApplicationLocation = new File(this.getClass().getClassLoader().getResource("testTransformation").getFile());
        when(transformation.getTransformedApplicationLocation()).thenReturn(transformedApplicationLocation);
        PowerMockito.mockStatic(FileUtils.class);
        compressionHandler.compress(transformation);
        Assert.assertTrue(new File(transformation.getTransformedApplicationLocation().getAbsolutePath() + ".zip").exists());
        Assert.assertTrue(new File(transformation.getTransformedApplicationLocation().getAbsolutePath()).exists());
    }

    @Test
    public void testCompressionWithInValidFilePath() {
        Assert.assertNotNull(transformation);
        transformedApplicationLocation = new File("test1_transformed");
        when(transformation.getTransformedApplicationLocation()).thenReturn(transformedApplicationLocation);
        compressionHandler.compress(transformation);
        Assert.assertFalse(new File(transformation.getTransformedApplicationLocation().getAbsolutePath() + ".zip").exists());
        Assert.assertFalse(new File(transformation.getTransformedApplicationLocation().getAbsolutePath()).exists());
    }

    @AfterMethod
    public void tearDown(){
        File compressedFile = new File(transformation.getTransformedApplicationLocation().getAbsolutePath() + ".zip");
        if(compressedFile.exists()) {
            boolean deleted = compressedFile.delete();
            logger.info("CompressionHandlerTest:tearDown: Removed Compressed File :" + deleted);
        }
    }
}
