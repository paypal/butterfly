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

public class PomRemoveDependencyTest extends TransformationUtilityTestHelper {

	private static final String DEPENDENCY_NOT_REMOVED_MSG = "Dependency com.test:not-present has NOT been removed from POM file /pom.xml because it is not present";

	@Test
	public void removeDependencyTest() throws IOException, XmlPullParserException {

		PomRemoveDependency uut = new PomRemoveDependency("org.springframework.boot", "spring-boot-dependencies")
				.relative("pom.xml");

		Assert.assertNotNull(getDependencyBeforeChange(uut));
		executeAndAssertSuccess(uut);
		Dependency dependencyAfterChange = getDependencyAfterChange(uut);
		Assert.assertNull(dependencyAfterChange);

	}

	@Test
	public void defaultNotPresentTest() throws IOException, XmlPullParserException {

		PomRemoveDependency uut = new PomRemoveDependency("com.test", "not-present").relative("pom.xml");

		Assert.assertNull(getDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
		assertExceptionOccurred(executionResult);

	}

	@Test
	public void failNotPresentTest() throws IOException, XmlPullParserException {

		PomRemoveDependency uut = new PomRemoveDependency("com.test", "not-present").relative("pom.xml")
				.failIfNotPresent();

		Assert.assertNull(getDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
		assertExceptionOccurred(executionResult);

	}

	@Test
	public void warnNotPresentTest() throws IOException, XmlPullParserException {

		PomRemoveDependency uut = new PomRemoveDependency("com.test", "not-present").relative("pom.xml")
				.warnIfNotPresent();

		Assert.assertNull(getDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
		assertExceptionOccurred(executionResult);

	}

	@Test
	public void noOpNotPresentTest() throws IOException, XmlPullParserException {

		PomRemoveDependency uut = new PomRemoveDependency("com.test", "not-present").relative("pom.xml")
				.noOpIfNotPresent();

		Assert.assertNull(getDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
		Assert.assertNull(executionResult.getException());
		Assert.assertEquals(executionResult.getDetails(), DEPENDENCY_NOT_REMOVED_MSG);

	}

	@Test
	public void getDescriptionTest() throws IOException, XmlPullParserException {

		PomRemoveDependency uut = new PomRemoveDependency("org.testng", "testng").relative("pom.xml");

		String description = uut.getDescription();
		Assert.assertEquals(description, "Remove dependency org.testng:testng from POM file pom.xml");

	}

	private Dependency getDependencyBeforeChange(PomRemoveDependency pomRemoveDependency)
			throws IOException, XmlPullParserException {
		Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
		Dependency dependencyBeforeChange = pomRemoveDependency.getDependency(pomModelBeforeChange);
		return dependencyBeforeChange;
	}

	private Dependency getDependencyAfterChange(PomRemoveDependency pomRemoveDependency)
			throws IOException, XmlPullParserException {
		Model pomModelAfterChange = getTransformedPomModel("pom.xml");
		Dependency dependencyAfterChange = pomRemoveDependency.getDependency(pomModelAfterChange);
		return dependencyAfterChange;
	}

	private void executeAndAssertSuccess(AbstractPomOperation<?> pomOperation) {
		TOExecutionResult executionResult = pomOperation.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
		Assert.assertEquals(executionResult.getDetails(),
				"Dependency org.springframework.boot:spring-boot-dependencies has been removed from POM file /pom.xml");
	}

	private void assertExceptionOccurred(TOExecutionResult executionResult) {
		Assert.assertNotNull(executionResult.getException());
		Assert.assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
		Assert.assertEquals(executionResult.getException().getMessage(), DEPENDENCY_NOT_REMOVED_MSG);
	}

}
