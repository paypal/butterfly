package com.paypal.butterfly.cli;

import com.paypal.butterfly.cli.logging.LogConfigurator;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.facade.ButterflyFacade;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.TransformationResult;
import com.test.SampleExtension;
import com.test.SampleTransformationTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

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

    // Even though this variable is not used explicitly in this class,
    // it is necessary to its proper execution, since the mock initialization
    // is happening regardless of it
    @SuppressWarnings("PMD.UnusedPrivateField")
    @Mock
    private LogConfigurator logConfigurator;

    private File sampleAppFolder;

    @BeforeMethod
    public void beforeTest() throws ButterflyException {
        TransformationResult mockResult = mock(TransformationResult.class);

        when(facade.transform(any(File.class), any(String.class))).thenReturn(mockResult);
        when(facade.transform(any(File.class), any(String.class), any(Configuration.class))).thenReturn(mockResult);
        when(facade.transform(any(File.class), any(Class.class))).thenReturn(mockResult);
        when(facade.transform(any(File.class), any(Class.class), any(Configuration.class))).thenReturn(mockResult);
        when(facade.transform(any(File.class), any(UpgradePath.class))).thenReturn(mockResult);
        when(facade.transform(any(File.class), any(UpgradePath.class), any(Configuration.class))).thenReturn(mockResult);

        File file = new File("");
        when(mockResult.getTransformedApplicationLocation()).thenReturn(file);
        when(mockResult.getManualInstructionsFile()).thenReturn(file);

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
     * To Test Transformation with -t, -v and -z options
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformation() throws IOException, ButterflyException {
        String arguments[] = {sampleAppFolder.getAbsolutePath(), "-t", "com.test.SampleTransformationTemplate", "-v", "-z"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(sampleAppFolder), eq(SampleTransformationTemplate.class), eq(new Configuration(null, true)));
    }

    /**
     * To Test Transformation with -s options
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformationWithShortcut() throws IOException, ButterflyException {
        when(facade.getRegisteredExtension()).thenReturn(new SampleExtension());

        String arguments[] = {sampleAppFolder.getAbsolutePath(), "-s", "2"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(sampleAppFolder), eq(SampleTransformationTemplate.class), eq(new Configuration(null, false)));
    }

    /**
     * To Test Transformation with -t and -s options.
     * Option -s should be ignored, since -t was also provided
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformationWithShortcutButIgnoringIt() throws IOException, ButterflyException {
        when(facade.getRegisteredExtension()).thenReturn(new SampleExtension());

        String arguments[] = {sampleAppFolder.getAbsolutePath(), "-t", "com.test.SampleTransformationTemplate", "-z", "-s", "2"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(sampleAppFolder), eq(SampleTransformationTemplate.class), eq(new Configuration(null, true)));
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
        String arguments[] = {sampleAppFolder.getAbsolutePath(), "-t", "com.test.SampleTransformationTemplate", "-v", "-o", "PATH_TO_OUTPUT_FOLDER"};
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
        String arguments[] = {sampleAppFolder.getAbsolutePath(), "-t", "com.test.SampleTransformationTemplate", "-v", "-o", currentDir};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(sampleAppFolder), eq(SampleTransformationTemplate.class), eq(new Configuration(new File(currentDir), false)));
    }

    @Test
    public void testAutomaticResolution() throws IOException, ButterflyException {
        doReturn(SampleTransformationTemplate.class).when(facade).automaticResolution(any(File.class));
        String arguments[] = {sampleAppFolder.getAbsolutePath()};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        verify(facade, times(1)).automaticResolution(eq(sampleAppFolder));
        verify(facade, times(1)).transform(eq(sampleAppFolder), eq(SampleTransformationTemplate.class), eq(new Configuration(null, false)));
        Assert.assertEquals(status, 0);
    }

    @Test
    public void testAutomaticResolutionFailed() throws IOException, ButterflyException {
        String arguments[] = {sampleAppFolder.getAbsolutePath()};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        verify(facade, times(1)).automaticResolution(eq(sampleAppFolder));
        verify(facade, times(0)).transform(eq(sampleAppFolder), eq(SampleTransformationTemplate.class), eq(new Configuration(null, false)));
        Assert.assertEquals(status, 1);
    }

    @Test
    public void testUnexistentApplicationFolder() throws IOException, ButterflyException {
        String arguments[] = {"unexistent_folder"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run().getExitStatus();

        verify(facade, times(0)).automaticResolution(eq(sampleAppFolder));
        verify(facade, times(0)).transform(eq(sampleAppFolder), eq(SampleTransformationTemplate.class), eq(new Configuration(null, false)));
        Assert.assertEquals(status, 1);
    }

}