package com.paypal.butterfly.core;

import com.paypal.butterfly.core.sample.SampleTransformationTemplate;
import org.apache.commons.io.FileUtils;
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

/**
 * CompressionHandlerTest
 *
 * Created by vkuncham on 10/31/2016.
 */
@PrepareForTest(FileUtils.class)
public class CompressionHandlerTest extends PowerMockTestCase {

    private static final Logger logger = LoggerFactory.getLogger(CompressionHandlerTest.class);
    private CompressionHandler compressionHandler = new CompressionHandler();
    private File transformedApplicationLocation;
    private TemplateTransformation transformation;

    @Test
    public void testCompressionWithValidFilePath() throws IOException {

        transformedApplicationLocation = new File(this.getClass().getClassLoader().getResource("testTransformation").getFile());
        transformation= new TemplateTransformation(new Application(transformedApplicationLocation),new SampleTransformationTemplate(),null);
        transformation.setTransformedApplicationLocation(transformedApplicationLocation);
        PowerMockito.mockStatic(FileUtils.class);
        compressionHandler.compress(transformation);
        Assert.assertTrue(new File(transformation.getTransformedApplicationLocation().getAbsolutePath() + ".zip").exists());
        Assert.assertTrue(new File(transformation.getTransformedApplicationLocation().getAbsolutePath()).exists());
    }

    @Test
    public void testCompressionWithInValidFilePath() {
        transformedApplicationLocation = new File(this.getClass().getClassLoader().getResource("testTransformation").getFile());
        transformation= new TemplateTransformation(new Application(transformedApplicationLocation),new SampleTransformationTemplate(),null);
        transformation.setTransformedApplicationLocation(new File("test1_transformed"));
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
