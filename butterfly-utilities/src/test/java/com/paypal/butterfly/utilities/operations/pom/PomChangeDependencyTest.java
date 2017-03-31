package com.paypal.butterfly.utilities.operations.pom;

import java.io.IOException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;

public class PomChangeDependencyTest extends TransformationUtilityTestHelper {

	@Test
	public void addVersionTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml")
				.setVersion("6.11");

		Assert.assertEquals(getDependencyBeforeChange(uut).getVersion(), null);
		executeAndAssertSuccess(uut);
		Assert.assertEquals(getDependencyAfterChange(uut).getVersion(), "6.11");

	}

	@Test
	public void changeVersionTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("org.apache.commons", "commons-lang3").relative("pom.xml")
				.setVersion("3.5");

		Assert.assertEquals(getDependencyBeforeChange(uut).getVersion(), "3.4");
		executeAndAssertSuccess(uut);
		Assert.assertEquals(getDependencyAfterChange(uut).getVersion(), "3.5");

	}

	@Test
	public void removeVersionTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("org.apache.commons", "commons-lang3").relative("pom.xml")
				.removeVersion();

		Assert.assertEquals(getDependencyBeforeChange(uut).getVersion(), "3.4");
		executeAndAssertSuccess(uut);
		Assert.assertNull(getDependencyAfterChange(uut).getVersion());

	}

	@Test
	public void addScopeTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("xmlunit", "xmlunit").relative("pom.xml").setScope("test");

		Assert.assertEquals(getDependencyBeforeChange(uut).getScope(), null);
		executeAndAssertSuccess(uut);
		Assert.assertEquals(getDependencyAfterChange(uut).getScope(), "test");

	}

	@Test
	public void changeScopeTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml")
				.setScope("compile");

		Assert.assertEquals(getDependencyBeforeChange(uut).getScope(), "test");
		executeAndAssertSuccess(uut);
		Assert.assertEquals(getDependencyAfterChange(uut).getScope(), "compile");

	}

	@Test
	public void removeScopeTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml").removeScope();

		Assert.assertEquals(getDependencyBeforeChange(uut).getScope(), "test");
		executeAndAssertSuccess(uut);
		Assert.assertEquals(getDependencyAfterChange(uut).getScope(), null);

	}

	@Test
	public void addTypeTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml").setType("pom");

		// Dependency with no type always returns "jar" as the type
		Assert.assertEquals(getDependencyBeforeChange(uut).getType(), "jar");
		executeAndAssertSuccess(uut);
		Assert.assertEquals(getDependencyAfterChange(uut).getType(), "pom");

	}

	@Test
	public void changeTypeTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("org.springframework.boot", "spring-boot-dependencies")
				.relative("pom.xml").setType("jar");

		Assert.assertEquals(getDependencyBeforeChange(uut).getType(), "pom");
		executeAndAssertSuccess(uut);
		Assert.assertEquals(getDependencyAfterChange(uut).getType(), "jar");

	}

	@Test
	public void removeTypeTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("org.springframework.boot", "spring-boot-dependencies")
				.relative("pom.xml").removeType();

		Assert.assertEquals(getDependencyBeforeChange(uut).getType(), "pom");
		executeAndAssertSuccess(uut);
		// Removing the type sets it to "jar"
		Assert.assertEquals(getDependencyAfterChange(uut).getType(), "jar");

	}

	@Test
	public void addOptionalTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml").setOptional();

		Assert.assertFalse(getDependencyBeforeChange(uut).isOptional());
		executeAndAssertSuccess(uut);
		Assert.assertTrue(getDependencyAfterChange(uut).isOptional());

	}

	@Test
	public void changeOptionalTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("junit", "junit")
				.relative("pom.xml").setOptional();

		Assert.assertFalse(getDependencyBeforeChange(uut).isOptional());
		executeAndAssertSuccess(uut);
		Assert.assertTrue(getDependencyAfterChange(uut).isOptional());

	}

	@Test
	public void removeOptionalTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("commons-io", "commons-io")
				.relative("pom.xml").removeOptional();

		Assert.assertTrue(getDependencyBeforeChange(uut).isOptional());
		executeAndAssertSuccess(uut);
		Assert.assertFalse(getDependencyAfterChange(uut).isOptional());

	}

	@Test
	public void defaultNotPresentTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("com.paypal", "not-present")
				.relative("pom.xml");

		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
		Assert.assertNotNull(executionResult.getException());
		Assert.assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);

	}

	@Test
	public void warnIfNotPresentTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("com.paypal", "not-present")
				.relative("pom.xml").warnIfNotPresent();

		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
		Assert.assertNotNull(executionResult.getException());
		Assert.assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);

	}

	@Test
	public void failIfNotPresentTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("com.paypal", "not-present")
				.relative("pom.xml").failIfNotPresent();

		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
		Assert.assertNotNull(executionResult.getException());
		Assert.assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);

	}

	@Test
	public void noOpIfNotPresentTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("com.paypal", "not-present")
				.relative("pom.xml").noOpIfNotPresent();

		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
		Assert.assertNull(executionResult.getException());

	}

	@Test
	public void getDescriptionTest() throws IOException, XmlPullParserException {

		PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml");

		String description = uut.getDescription();
		Assert.assertEquals(description, "Change dependency org.testng:testng in POM file pom.xml");

	}

	private Dependency getDependencyBeforeChange(PomChangeDependency pomChangeDependency)
			throws IOException, XmlPullParserException {
		Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
		Dependency dependencyBeforeChange = pomChangeDependency.getDependency(pomModelBeforeChange);
		Assert.assertNotNull(dependencyBeforeChange);
		return dependencyBeforeChange;
	}

	private Dependency getDependencyAfterChange(PomChangeDependency pomChangeDependency)
			throws IOException, XmlPullParserException {
		Model pomModelAfterChange = getTransformedPomModel("pom.xml");
		Dependency dependencyAfterChange = pomChangeDependency.getDependency(pomModelAfterChange);
		Assert.assertNotNull(dependencyAfterChange);
		return dependencyAfterChange;
	}

	private void executeAndAssertSuccess(AbstractPomOperation<?> pomOperation) {
		TOExecutionResult executionResult = pomOperation.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
	}

}
