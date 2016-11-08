package com.paypal.butterfly.core;

import com.paypal.butterfly.core.exception.InternalException;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.facade.exception.TemplateResolutionException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
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

    @InjectMocks
    private ButterflyFacadeImpl butterflyFacadeImpl;

    @Mock
    private ExtensionRegistry extensionRegistry;

    @Mock
    private TransformationEngine transformationEngine;

    private ExtensionRegistry extensionRegistry_test = new ExtensionRegistry();
    private File applicationFolder = new File(System.getProperty("user.dir") + "\\testTransformation");


    @Test
    public void testGetRegisteredExtensions() {
        when(extensionRegistry.getExtensions()).thenReturn(extensionRegistry_test.getExtensions());
        List<Extension> list = butterflyFacadeImpl.getRegisteredExtensions();
        Extension extension = list.get(0);
        Assert.assertTrue(extension instanceof ExtensionSampleOne);
    }

    @Test
    public void testAutomaticResolutionAsNull() {
        try {
            when(extensionRegistry.getExtensions()).thenReturn(extensionRegistry_test.getExtensions());
            Assert.assertEquals(null ,butterflyFacadeImpl.automaticResolution(new File(System.getProperty("user.dir") + "\\testTransformation1")));
        }catch(TemplateResolutionException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testAutomaticResolutionAsNotNull() {
        try {
            when(extensionRegistry.getExtensions()).thenReturn(extensionRegistry_test.getExtensions());
            Assert.assertEquals(SampleTransformationTemplate.class,butterflyFacadeImpl.automaticResolution(applicationFolder));
        }catch(TemplateResolutionException ex) {
            ex.printStackTrace();
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformWithTemplateAsEmptyString() {
        try {
            butterflyFacadeImpl.transform(applicationFolder,"");
        }catch(ButterflyException ex) {
            ex.printStackTrace();
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformWithTemplateAsNull() {
        try {
            butterflyFacadeImpl.transform(applicationFolder,(String) null);
        }catch(ButterflyException ex) {
            ex.printStackTrace();
        }
    }


    @Test(expectedExceptions = InternalException.class)
    public void testTransformWithInValidTemplate() {
        try {
            butterflyFacadeImpl.transform(applicationFolder,"TestTemplate");
        }catch(ButterflyException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testTransformWithValidTemplate() {
        try {
            boolean appFolderExists = applicationFolder.mkdirs();
            if(appFolderExists) {
                butterflyFacadeImpl.transform(applicationFolder, "com.paypal.butterfly.core.SampleTransformationTemplate");
            } else {
                Assert.assertTrue(false);
            }
        }catch(ButterflyException ex) {
            ex.printStackTrace();
        }
    }


    @Test(expectedExceptions = InternalException.class)
    public void testTransformWithAbstractTemplate() {
        try {
            butterflyFacadeImpl.transform(applicationFolder,"com.paypal.butterfly.core.SampleAbstractTransformationTemplate");
        }catch(ButterflyException ex) {
            ex.printStackTrace();
        }
    }


    @Test
    public void testTransformWithValidTemplateAsClass() {
        try {
            boolean appFolderExists = applicationFolder.mkdirs();
            if(appFolderExists) {
                butterflyFacadeImpl.transform(applicationFolder,SampleTransformationTemplate.class);
            }else {
                Assert.assertTrue(false);
            }
        }catch(ButterflyException ex) {
            ex.printStackTrace();
        }
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformWithValidUpgradePathInvalidAppFolder() {
        try {
            UpgradePath  upgradePath = new UpgradePath(SampleUpgradeStep.class);
            butterflyFacadeImpl.transform(applicationFolder,upgradePath);
        }catch(ButterflyException ex) {
            ex.printStackTrace();
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransformWithInValidUpgradePath() {
        try {
            boolean appFolderExists = applicationFolder.mkdirs();
            if(appFolderExists) {
                butterflyFacadeImpl.transform(applicationFolder, (UpgradePath) null);
            }else {
                Assert.assertTrue(false);
            }

        }catch(ButterflyException ex) {
            ex.printStackTrace();
        }
    }


    @Test
    public void testTransformWithValidUpgradePath() {
        try {
            boolean appFolderExists = applicationFolder.mkdirs();
            if(appFolderExists) {
                UpgradePath  upgradePath = new UpgradePath(SampleUpgradeStep.class);
                butterflyFacadeImpl.transform(applicationFolder,upgradePath);
            } else {
                Assert.assertTrue(false);
            }

        }catch(ButterflyException ex) {
            ex.printStackTrace();
        }
    }


    @AfterMethod
    public void tearDown() {
        boolean deleted = applicationFolder.exists()? applicationFolder.delete():false;
        System.out.println("In tearDown:" + deleted);
    }

}
