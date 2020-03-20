package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.*;
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
 * {@link TransformationUtility} and its
 * sub-classes
 *
 * @author facarvalho
 */
@SuppressWarnings("WeakerAccess")
public abstract class TestHelper {

    protected File appFolder;
    protected File transformedAppFolder;
    protected TransformationContext transformationContext;

    @BeforeClass
    public void beforeClass() throws URISyntaxException {
        appFolder = new File(getClass().getResource("/test-app-1").toURI());
    }

    @BeforeMethod
    public void beforeMethod(Method method) throws IOException {
        transformedAppFolder = new File(appFolder.getParentFile(), String.format("test-app_%s_%s_%s", method.getDeclaringClass().getSimpleName(), method.getName(), System.currentTimeMillis()));
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed app folder: %s\n", transformedAppFolder.getAbsolutePath());
        transformationContext = Mockito.mock(TransformationContext.class);
    }

    /*
     * Returns a sample TU that just returns a File object referencing the file it is supposed to work with
     */
    protected TransformationUtility getNewTestTransformationUtility() {
        return new TransformationUtility() {
            @Override
            public String getDescription() {
                return "Test transformation utility";
            }
            @Override
            protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
                File file = getAbsoluteFile(transformedAppFolder, transformationContext);
                return TUExecutionResult.value(this, file);
            }
        };
    }

    private static class SampleExtension extends Extension {
        @Override
        public String getDescription() {
            return null;
        }
        @Override
        public String getVersion() {
            return null;
        }
    }

    protected TransformationTemplate getNewTestTransformationTemplate() {
        return new TransformationTemplate() {
            @Override
            public Class<? extends Extension> getExtensionClass() {
                return SampleExtension.class;
            }
            @Override
            public String getDescription() {
                return "Test transformation template";
            }
        };
    }

}
