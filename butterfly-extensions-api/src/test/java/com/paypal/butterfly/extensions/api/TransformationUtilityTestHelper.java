package com.paypal.butterfly.extensions.api;

import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

/**
 * Helper class to write unit tests for
 * {@link TransformationUtility}
 * sub-classes
 *
 * @author facarvalho
 */
public abstract class TransformationUtilityTestHelper {

    protected File appFolder;
    protected File transformedAppFolder;
    protected TransformationContext transformationContext;

    @BeforeClass
    public void beforeClass() throws URISyntaxException, IOException {
        appFolder = new File(getClass().getResource("/test-app").toURI());
    }

    @BeforeMethod
    public void beforeMethod(Method method) throws URISyntaxException, IOException {
        transformedAppFolder = new File(appFolder.getParentFile(), String.format("test-app_%s_%s_%s", method.getDeclaringClass().getSimpleName(), method.getName(), System.currentTimeMillis()));
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed app folder: %s\n", transformedAppFolder.getAbsolutePath());
        transformationContext = Mockito.mock(TransformationContext.class);
    }

}
