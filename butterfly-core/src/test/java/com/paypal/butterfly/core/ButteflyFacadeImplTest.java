package com.paypal.butterfly.core;

import com.paypal.butterfly.core.exception.InternalException;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.facade.exception.TemplateResolutionException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * ButteflyFacadeImplTest
 *
 * Created by vkuncham on 11/7/2016.
 */
public class ButteflyFacadeImplTest extends PowerMockTestCase {


    private static final Logger logger = LoggerFactory.getLogger(ButteflyFacadeImplTest.class);

    @InjectMocks
    private ButterflyFacadeImpl butterflyFacadeImpl;

    @Mock
    private ExtensionRegistry extensionRegistry;

    @Mock
    private TransformationEngine transformationEngine;

    private ExtensionRegistry extensionRegistry_test = new ExtensionRegistry();

    private File applicationFolder = new File(this.getClass().getClassLoader().getResource("testTransformation").getFile());


    @Test
    public void testGetRegisteredExtensions() {
        when(extensionRegistry.getExtensions()).thenReturn(extensionRegistry_test.getExtensions());
        List<Extension> list = butterflyFacadeImpl.getRegisteredExtensions();
        Extension extension = list.get(0);
        Assert.assertTrue(extension instanceof ExtensionSampleOne);
    }

    @Test
    public void testAutomaticResolutionAsNull() throws TemplateResolutionException {
      when(extensionRegistry.getExtensions()).thenReturn(extensionRegistry_test.getExtensions());
      Assert.assertEquals(null ,butterflyFacadeImpl.automaticResolution(new File("testTransformation1")));
    }

    @Test
    public void testAutomaticResolutionAsNotNull() throws TemplateResolutionException {
      when(extensionRegistry.getExtensions()).thenReturn(extensionRegistry_test.getExtensions());
      Assert.assertEquals(SampleTransformationTemplate.class,butterflyFacadeImpl.automaticResolution(applicationFolder));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformWithTemplateAsEmptyString() throws ButterflyException {
       butterflyFacadeImpl.transform(applicationFolder,"");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformWithTemplateAsNull() throws ButterflyException {
        butterflyFacadeImpl.transform(applicationFolder,(String) null);

    }


    @Test(expectedExceptions = InternalException.class)
    public void testTransformWithInValidTemplate() throws ButterflyException {
      butterflyFacadeImpl.transform(applicationFolder,"TestTemplate");
    }

    @Test
    public void testTransformWithValidTemplate() throws ButterflyException {
         butterflyFacadeImpl.transform(applicationFolder, "com.paypal.butterfly.core.SampleTransformationTemplate");
    }


    @Test(expectedExceptions = InternalException.class)
    public void testTransformWithAbstractTemplate() throws ButterflyException {
         butterflyFacadeImpl.transform(applicationFolder,"com.paypal.butterfly.core.SampleAbstractTransformationTemplate");
    }

    @Test
    public void testTransformWithValidTemplateAsClass() throws ButterflyException {
         butterflyFacadeImpl.transform(applicationFolder,SampleTransformationTemplate.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformWithValidUpgradePathInvalidAppFolder() throws ButterflyException {
        UpgradePath  upgradePath = new UpgradePath(SampleUpgradeStep.class);
        butterflyFacadeImpl.transform(new File("testTransformation1"),upgradePath);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformWithInValidUpgradePath() throws ButterflyException {
          butterflyFacadeImpl.transform(applicationFolder, (UpgradePath) null);
    }

    @Test
    public void testTransformWithValidUpgradePath() throws ButterflyException {
        UpgradePath  upgradePath = new UpgradePath(SampleUpgradeStep.class);
        butterflyFacadeImpl.transform(applicationFolder,upgradePath);
    }
}
