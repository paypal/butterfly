package com.paypal.butterfly.cli;

import com.paypal.butterfly.cli.logging.LogConfigurator;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.facade.ButterflyFacade;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.TransformationResult;
import com.test.SampleExtension;
import com.test.SampleTransformationTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Butterfly Command Line Interface test
 *
 * @author facarvalho
 */
public class ButterflyCliTest extends PowerMockTestCase {

    @InjectMocks
    private ButterflyCliRunner butterflyCli;

    @Mock
    private ButterflyFacade facade;

    @Mock
    private LogConfigurator logConfigurator;

    private File sampleAppFolder;

    @BeforeMethod
    public void beforeTest() throws ButterflyException {
        TransformationResult mockResult = Mockito.mock(TransformationResult.class);

        Mockito.when(facade.transform(Mockito.any(File.class), Mockito.any(String.class))).thenReturn(mockResult);
        Mockito.when(facade.transform(Mockito.any(File.class), Mockito.any(String.class), Mockito.any(Configuration.class))).thenReturn(mockResult);
        Mockito.when(facade.transform(Mockito.any(File.class), Mockito.any(Class.class))).thenReturn(mockResult);
        Mockito.when(facade.transform(Mockito.any(File.class), Mockito.any(Class.class), Mockito.any(Configuration.class))).thenReturn(mockResult);
        Mockito.when(facade.transform(Mockito.any(File.class), Mockito.any(UpgradePath.class))).thenReturn(mockResult);
        Mockito.when(facade.transform(Mockito.any(File.class), Mockito.any(UpgradePath.class), Mockito.any(Configuration.class))).thenReturn(mockResult);

        File file = new File("");
        Mockito.when(mockResult.getTransformedApplicationLocation()).thenReturn(file);
        Mockito.when(mockResult.getManualInstructionsFile()).thenReturn(file);

        sampleAppFolder = new File(this.getClass().getResource("/sample_app").getFile());
    }

    /**
     * To Test listing of extensions
     *
     * @throws IOException
     */
    @Test
    public void testListingExtensions() throws IOException {
        Assert.assertNotNull(butterflyCli);
        Assert.assertNotNull(facade);

        String[] arguments = {"-l", "-v"};
        butterflyCli.setOptionSet(arguments);
        
        int status = butterflyCli.run().getExitStatus();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).getRegisteredExtension();
    }

    /**
     * To Test Transformation with -i, -t, -v and -z options
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformation() throws IOException, ButterflyException {
        String arguments[] = {"-i", sampleAppFolder.getAbsolutePath(), "-t", "com.test.SampleTransformationTemplate", "-v", "-z"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(sampleAppFolder), eq(com.test.SampleTransformationTemplate.class), eq(new Configuration(null, true)));
    }

    /**
     * To Test Transformation with -i and -s options
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformationWithShortcut() throws IOException, ButterflyException {
        Mockito.when(facade.getRegisteredExtension()).thenReturn(new SampleExtension());

        String arguments[] = {"-i", sampleAppFolder.getAbsolutePath(), "-s", "2"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(sampleAppFolder), eq(com.test.SampleTransformationTemplate.class), eq(new Configuration(null, false)));
    }

    /**
     * To Test Transformation with -i, -t and -s options.
     * Option -s should be ignored, since -t was also provided
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformationWithShortcutButIgnoringIt() throws IOException, ButterflyException {
        Mockito.when(facade.getRegisteredExtension()).thenReturn(new SampleExtension());

        String arguments[] = {"-i", sampleAppFolder.getAbsolutePath(), "-t", "com.test.SampleTransformationTemplate", "-z", "-s", "2"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(sampleAppFolder), eq(com.test.SampleTransformationTemplate.class), eq(new Configuration(null, true)));
        verify(facade, times(0)).getRegisteredExtension();
    }

    /**
     * To Test without using any option, so that help option would be used
     *
     * @throws IOException
     */
    @Test
    public void testNoOptions() throws IOException {
        int status = butterflyCli.run().getExitStatus();
        Assert.assertEquals(status, 0);
    }

    /**
     * To Test the case where exception is expected when non existing directory is being used as output directory
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformationWithNonExistDir() throws IOException, ButterflyException {
        String arguments[] = {"-i", sampleAppFolder.getAbsolutePath(), "-t", "com.test.SampleTransformationTemplate", "-v", "-o", "PATH_TO_OUTPUT_FOLDER"};
        butterflyCli.setOptionSet(arguments);
        butterflyCli.run();
    }

    /**
     * To test Transformation with the output directory that actually should exist. Hence current directory is being used as an output directory
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformationWithValidOutPutDir() throws IOException, ButterflyException {
        String currentDir = System.getProperty("user.dir");
        String arguments[] = {"-i", sampleAppFolder.getAbsolutePath(), "-t", "com.test.SampleTransformationTemplate", "-v", "-o", currentDir};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(sampleAppFolder), eq(com.test.SampleTransformationTemplate.class), eq(new Configuration(new File(currentDir), false)));
    }

    @Test
    public void testAutomaticResolution() throws IOException, ButterflyException {
        Mockito.doReturn(SampleTransformationTemplate.class).when(facade).automaticResolution(Mockito.any(File.class));
        String arguments[] = {"-i", sampleAppFolder.getAbsolutePath(), "-a"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        verify(facade, times(1)).automaticResolution(eq(sampleAppFolder));
        verify(facade, times(1)).transform(eq(sampleAppFolder), eq(SampleTransformationTemplate.class), eq(new Configuration(null, false)));
        Assert.assertEquals(status, 0);
    }

    @Test
    public void testAutomaticResolutionFailed() throws IOException, ButterflyException {
        String arguments[] = {"-i", sampleAppFolder.getAbsolutePath(), "-a"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        verify(facade, times(1)).automaticResolution(eq(sampleAppFolder));
        verify(facade, times(0)).transform(eq(sampleAppFolder), eq(SampleTransformationTemplate.class), eq(new Configuration(null, false)));
        Assert.assertEquals(status, 1);
    }

    @Test
    public void testUnexistentApplicationFolder() throws IOException, ButterflyException {
        String arguments[] = {"-i", "unexistent_folder", "-a"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        verify(facade, times(0)).automaticResolution(eq(sampleAppFolder));
        verify(facade, times(0)).transform(eq(sampleAppFolder), eq(SampleTransformationTemplate.class), eq(new Configuration(null, false)));
        Assert.assertEquals(status, 1);
    }

}