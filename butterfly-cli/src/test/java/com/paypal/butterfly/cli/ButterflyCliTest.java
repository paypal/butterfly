package com.paypal.butterfly.cli;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.facade.ButterflyFacade;
import com.paypal.butterfly.facade.Configuration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

/**
 * Butterfly Command Line Interface test
 *
 * @author facarvalho
 */
public class ButterflyCliTest extends PowerMockTestCase {

    @InjectMocks
    private ButterflyCli butterflyCli;

    @Mock
    private ButterflyFacade facade;

    @Mock
    private VerboseConfigurator verboseConfigurator;

    /**
     * To Test listing of extensions.
     * @throws IOException
     */
    @Test
    public void testListingExtensions() throws IOException {
        Assert.notNull(butterflyCli);
        Assert.notNull(facade);

        Set<Extension> extensions = new HashSet<>();
        when(facade.getRegisteredExtensions()).thenReturn(extensions);

        String[] arguments = {"-l", "-v"};
        butterflyCli.run(arguments);

        verify(facade, times(2)).getRegisteredExtensions();
    }

    /**
     * To Test Transformation with -i, -t, -v and -z options.
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformation() throws IOException, ButterflyException {
        String arguments[] = {"-i", "PATH_TO_APP_FOLDER", "-t", "com.test.SampleTransformationTemplate", "-v", "-z"};
        butterflyCli.run(arguments);
        verify(facade, times(1)).transform(eq(new File("PATH_TO_APP_FOLDER")), eq("com.test.SampleTransformationTemplate"), eq(new Configuration(null, true)));
    }

    /**
     * To Test without using any option, so that help option would be used.
     * @throws IOException
     */
    @Test
    public void testNoOptions() throws IOException {
        String[] arguments = {};
        butterflyCli.run(arguments);
    }

    /**
     * To Test Butterfly Version
     */
    @Test
    public void testGetButterflyVersionFromProperties(){
        String version = VersionHelper.getButterflyVersion();
        Assert.notNull(version);
    }

    /**
     * To Test the case where exception is expected when non existing directory is being used as output directory.
     * @throws IOException
     * @throws ButterflyException
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformationWithNonExistDir() throws IOException, ButterflyException {
        String arguments[] = {"-i", "PATH_TO_APP_FOLDER", "-t", "com.test.SampleTransformationTemplate", "-v", "-o", "PATH_TO_OUTPUT_FOLDER"};
        butterflyCli.run(arguments);
    }

    /**
     * To test Transformation with the output directory that actually should exist. Hence current directory is being used as an output directory.
     * @throws IOException
     * @throws ButterflyException
     */
    @Test
    public void testTransformationWithValidOutPutDir() throws IOException, ButterflyException {
        String currentDir = System.getProperty("user.dir");
        String arguments[] = {"-i", "PATH_TO_APP_FOLDER", "-t", "com.test.SampleTransformationTemplate", "-v", "-o", currentDir};
        butterflyCli.run(arguments);
        verify(facade, times(1)).transform(eq(new File("PATH_TO_APP_FOLDER")), eq("com.test.SampleTransformationTemplate"), eq(new Configuration(new File(currentDir), false)));
    }

}