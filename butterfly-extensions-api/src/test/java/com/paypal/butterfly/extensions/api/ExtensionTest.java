package com.paypal.butterfly.extensions.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Model;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ExtensionTest {

	private Extension<Object> uut;

	@BeforeClass
	public void before() {
		uut = new Extension<Object>() {
			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public String getVersion() {
				return null;
			}

			@Override
			public Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) {
				return null;
			}
		};
	}

	@Test
	public void goodPomTest() throws FileNotFoundException {
		Model model = uut.getRootPomFile(getFolder("/testapps/goodPom"));
		Assert.assertNotNull(model);
	}

	@Test
	public void noPomTest() throws FileNotFoundException {
		Model model = uut.getRootPomFile(getFolder("/testapps/noPom"));
		Assert.assertNull(model);
	}

	@Test
	public void badPomTest() throws FileNotFoundException {
		Model model = uut.getRootPomFile(getFolder("/testapps/badPom"));
		Assert.assertNull(model);
	}

	@Test
	public void templateClassesTest() {
		uut.add(Class1.class);
		uut.add(Class2.class);
		uut.add(Class3.class);
		List<Class<? extends TransformationTemplate>> templateList = uut.getTemplateClasses();
		Assert.assertTrue(templateList.containsAll(Arrays.asList(Class1.class, Class2.class, Class3.class)));
	}
	
	private File getFolder(String folderPath) throws FileNotFoundException {
		URL url = ExtensionTest.class.getResource(folderPath);
		if (url != null) {
			try {
				File folder = new File(url.toURI());
				return folder;
			} catch (URISyntaxException e) {
			}
		}
		throw new FileNotFoundException("App folder " + folderPath + " not found in classpath");
	}

	private static class TemplateBase extends TransformationTemplate {

		@Override
		public Class<? extends Extension<?>> getExtensionClass() {
			return null;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getApplicationType() {
			return null;
		}

		@Override
		public String getApplicationName() {
			return null;
		}}

	private static class Class1 extends TemplateBase {}
	private static class Class2 extends TemplateBase {}
	private static class Class3 extends TemplateBase {}

}
