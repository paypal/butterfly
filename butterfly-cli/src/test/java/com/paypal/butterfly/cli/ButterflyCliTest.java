package com.paypal.butterfly.cli;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.facade.ButterflyFacade;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Butterfly Command Line Interface test
 *
 * @author facarvalho
 */
public class ButterflyCliTest extends PowerMockTestCase {

    @InjectMocks
    private ButterflyCli cli;

    @Mock
    private ButterflyFacade facade;

    @Test
    public void testListingExtensions() throws IOException {
        Assert.notNull(cli);
        Assert.notNull(facade);

        Set<Extension> extensions = new HashSet<>();
        when(facade.getRegisteredExtensions()).thenReturn(extensions);

        String[] arguments = {"-l"};
        cli.run(arguments);

        verify(facade, times(1)).getRegisteredExtensions();
    }

}