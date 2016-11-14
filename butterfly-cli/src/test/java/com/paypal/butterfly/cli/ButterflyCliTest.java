package com.paypal.butterfly.cli;

import com.paypal.butterfly.cli.logging.LogConfigurator;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.facade.ButterflyFacade;
import com.paypal.butterfly.facade.Configuration;
import com.test.SampleExtension;
import com.test.SampleTransformationTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
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

    /**
     * To Test listing of extensions
     *
     * @throws IOException
     */
    @Test
    public void testListingExtensions() throws IOException {
        Assert.assertNotNull(butterflyCli);
        Assert.assertNotNull(facade);

        List<Extension> extensions = new ArrayList<>();
        when(facade.getRegisteredExtensions()).thenReturn(extensions);

        String[] arguments = {"-l", "-v"};
        butterflyCli.setOptionSet(arguments);
        
        int status = butterflyCli.run();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).getRegisteredExtensions();
    }

    /**
     * To Test Transformation with -i, -t, -v and -z options
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformation() throws IOException, ButterflyException {
        String arguments[] = {"-i", "PATH_TO_APP_FOLDER", "-t", "com.test.SampleTransformationTemplate", "-v", "-z"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(new File("PATH_TO_APP_FOLDER")), eq(com.test.SampleTransformationTemplate.class), eq(new Configuration(null, true)));
    }

    /**
     * To Test Transformation with -i and -s options
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformationWithShortcut() throws IOException, ButterflyException {
        List<Extension> extensionsList = new ArrayList<>();
        extensionsList.add(new SampleExtension());
        Mockito.when(facade.getRegisteredExtensions()).thenReturn(extensionsList);

        String arguments[] = {"-i", "PATH_TO_APP_FOLDER", "-s", "2"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(new File("PATH_TO_APP_FOLDER")), eq(com.test.SampleTransformationTemplate.class), eq(new Configuration(null, false)));
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
        List<Extension> extensionsList = new ArrayList<>();
        extensionsList.add(new SampleExtension());
        Mockito.when(facade.getRegisteredExtensions()).thenReturn(extensionsList);

        String arguments[] = {"-i", "PATH_TO_APP_FOLDER", "-t", "com.test.SampleTransformationTemplate", "-z", "-s", "2"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(new File("PATH_TO_APP_FOLDER")), eq(com.test.SampleTransformationTemplate.class), eq(new Configuration(null, true)));
        verify(facade, times(0)).getRegisteredExtensions();
    }

    /**
     * To Test without using any option, so that help option would be used
     *
     * @throws IOException
     */
    @Test
    public void testNoOptions() throws IOException {
        int status = butterflyCli.run();
        Assert.assertEquals(status, 0);
    }

    /**
     * To Test Butterfly Version
     */
    @Test
    public void testGetButterflyVersionFromProperties(){
        String version = VersionHelper.getButterflyVersion();
        Assert.assertNotNull(version);
    }

    /**
     * To Test the case where exception is expected when non existing directory is being used as output directory
     *
     * @throws IOException
     * @throws ButterflyException
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformationWithNonExistDir() throws IOException, ButterflyException {
        String arguments[] = {"-i", "PATH_TO_APP_FOLDER", "-t", "com.test.SampleTransformationTemplate", "-v", "-o", "PATH_TO_OUTPUT_FOLDER"};
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
        String arguments[] = {"-i", "PATH_TO_APP_FOLDER", "-t", "com.test.SampleTransformationTemplate", "-v", "-o", currentDir};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run();

        Assert.assertEquals(status, 0);
        verify(facade, times(1)).transform(eq(new File("PATH_TO_APP_FOLDER")), eq(com.test.SampleTransformationTemplate.class), eq(new Configuration(new File(currentDir), false)));
    }

    @Test
    public void testAutomaticResolution() throws IOException, ButterflyException {
        Mockito.doReturn(SampleTransformationTemplate.class).when(facade).automaticResolution(Mockito.any(File.class));
        String arguments[] = {"-i", "PATH_TO_APP_FOLDER", "-a"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run();

        verify(facade, times(1)).automaticResolution(eq(new File("PATH_TO_APP_FOLDER")));
        verify(facade, times(1)).transform(eq(new File("PATH_TO_APP_FOLDER")), eq(SampleTransformationTemplate.class), eq(new Configuration(null, false)));
        Assert.assertEquals(status, 0);
    }

    @Test
    public void testAutomaticResolutionFailed() throws IOException, ButterflyException {
        String arguments[] = {"-i", "PATH_TO_APP_FOLDER", "-a"};
        butterflyCli.setOptionSet(arguments);
        int status = butterflyCli.run();

        verify(facade, times(1)).automaticResolution(eq(new File("PATH_TO_APP_FOLDER")));
        verify(facade, times(0)).transform(eq(new File("PATH_TO_APP_FOLDER")), eq(SampleTransformationTemplate.class), eq(new Configuration(null, false)));
        Assert.assertEquals(status, 1);
    }

}