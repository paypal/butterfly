package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.*;

/**
 * Unit tests for {@link Extension}
 *
 * @author facarvalho
 */
public class ExtensionTest {

    @Test
	public void basicTest() {
		Extension extension = new SampleExtension();

		assertEquals(extension.getDescription(), "Test extension");
        assertEquals(extension.getVersion(), "1.0.0");
        assertEquals(extension.toString(), "com.paypal.butterfly.extensions.api.ExtensionTest$SampleExtension");
	}

	@Test
    public void templatesTest() {
        Extension extension = new SampleExtension();

        assertNotNull(extension.getTemplateClasses());
        assertEquals(extension.getTemplateClasses().size(), 0);

        extension.add(SampleTransformationTemplate1.class);
        assertEquals(extension.getTemplateClasses().size(), 1);
        assertEquals(extension.getTemplateClasses().get(0), SampleTransformationTemplate1.class);

        extension.add(SampleTransformationTemplate2.class);
        extension.add(SampleTransformationTemplate3.class);
        assertEquals(extension.getTemplateClasses().size(), 3);
        assertTrue(extension.getTemplateClasses().contains(SampleTransformationTemplate1.class));
        assertTrue(extension.getTemplateClasses().contains(SampleTransformationTemplate2.class));
        assertTrue(extension.getTemplateClasses().contains(SampleTransformationTemplate3.class));
    }

    @Test
    public void automaticResolutionTest() throws URISyntaxException, TemplateResolutionException {
        File appFolder = new File(getClass().getResource("/test-app").toURI());

        Extension extension = new Extension() {
            @Override
            public String getDescription() {return null;}
            @Override
            public String getVersion() {return null;}

            @Override
            public Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) throws TemplateResolutionException {
                return (Class<? extends TransformationTemplate>) getTemplateClasses().get(0);
            }
        };
        extension.add(SampleTransformationTemplate2.class);

        assertEquals(extension.automaticResolution(appFolder), SampleTransformationTemplate2.class);
    }

    @Test(expectedExceptions = TemplateResolutionException.class, expectedExceptionsMessageRegExp = "No transformation template could be resolved")
    public void notSupportedAutomaticResolutionTest() throws URISyntaxException, TemplateResolutionException {
        File appFolder = new File(getClass().getResource("/test-app").toURI());
        new SampleExtension().automaticResolution(appFolder);
    }

    @Test
    public void rootPomFileTest() throws URISyntaxException, IOException, XmlPullParserException {
        Extension extension = new SampleExtension();
        Model rootPomFile = extension.getRootPomFile(new File(getClass().getResource("/sample_pom_files/goodPom").toURI()));

        assertNotNull(rootPomFile);
        assertEquals(rootPomFile.getGroupId(), "com.test.123");
        assertEquals(rootPomFile.getArtifactId(), "test123");
        assertEquals(rootPomFile.getVersion(), "1.0.0");
        assertEquals(rootPomFile.getPackaging(), "pom");
        assertEquals(rootPomFile.getName(), "test application");
    }

    @Test(expectedExceptions = XmlPullParserException.class)
    public void parseErrorRootPomFileTest() throws URISyntaxException, IOException, XmlPullParserException {
        new SampleExtension().getRootPomFile(new File(getClass().getResource("/sample_pom_files/badPom").toURI()));
    }

    @Test(expectedExceptions = FileNotFoundException.class)
    public void noRootPomFileTest() throws URISyntaxException, IOException, XmlPullParserException {
        new SampleExtension().getRootPomFile(new File(getClass().getResource("/sample_pom_files/noPom").toURI()));
    }

    private static abstract class BaseSampleTransformationTemplate extends TransformationTemplate {
        @Override
        public Class<? extends Extension> getExtensionClass() {
            return Extension.class;
        }
        @Override
        public String getDescription() {
            return "Test transformation template";
        }
    }

    private static class SampleExtension extends Extension {
        @Override
        public String getDescription() {
            return "Test extension";
        }
        @Override
        public String getVersion() {
            return "1.0.0";
        }
    }

    private static class SampleTransformationTemplate1 extends BaseSampleTransformationTemplate {}
    private static class SampleTransformationTemplate2 extends BaseSampleTransformationTemplate {}
    private static class SampleTransformationTemplate3 extends BaseSampleTransformationTemplate {}

}
