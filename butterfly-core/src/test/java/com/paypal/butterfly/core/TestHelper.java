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
public abstract class TestHelper {

    protected File appFolder;
    protected File transformedAppFolder;
    protected TransformationContext transformationContext;

    @BeforeClass
    public void beforeClass() throws URISyntaxException, IOException {
        appFolder = new File(getClass().getResource("/test-app-1").toURI());
    }

    @BeforeMethod
    public void beforeMethod(Method method) throws URISyntaxException, IOException {
        transformedAppFolder = new File(appFolder.getParentFile(), String.format("test-app_%s_%s_%s", method.getDeclaringClass().getSimpleName(), method.getName(), System.currentTimeMillis()));
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed app folder: %s\n", transformedAppFolder.getAbsolutePath());
        transformationContext = Mockito.mock(TransformationContext.class);
    }

    protected UtilityCondition getNewTestUtilityCondition(boolean result) {
        return new UtilityCondition() {
            @Override
            public String getDescription() {
                return "Test utility condition";
            }
            @Override
            protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
                return TUExecutionResult.value(this, result);
            }
        };
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

    /*
     * Returns a sample NO_OP TO
     */
    protected TransformationOperation<TransformationOperation> getNewTestTransformationOperation() {
        return new TransformationOperation() {
            @Override
            public String getDescription() {
                return "Test transformation operation";
            }
            @Override
            protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
                return TOExecutionResult.noOp(this, "nothing to be changed");
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
