package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public class PomRemoveDependencyTest extends TransformationUtilityTestHelper {

	private static final String DEPENDENCY_NOT_REMOVED_MSG = "Dependency com.test:not-present has NOT been removed from POM file /pom.xml because it is not present";

	@Test
	public void miscTest() throws CloneNotSupportedException {
		PomRemoveDependency pomRemoveDependency = new PomRemoveDependency("org.springframework.boot", "spring-boot-dependencies").relative("pom.xml");

		assertEquals(pomRemoveDependency.getDescription(), "Remove dependency org.springframework.boot:spring-boot-dependencies from POM file pom.xml");
		assertEquals(pomRemoveDependency.clone(), pomRemoveDependency);
	}

	@Test
	public void removeDependencyTest() throws IOException, XmlPullParserException {
		PomRemoveDependency uut = new PomRemoveDependency("org.springframework.boot", "spring-boot-dependencies").relative("pom.xml");

		assertNotNull(getDependencyBeforeChange(uut));
		executeAndAssertSuccess(uut);
		Dependency dependencyAfterChange = getDependencyAfterChange(uut);
		assertNull(dependencyAfterChange);
	}

	@Test
	public void defaultNotPresentTest() throws IOException, XmlPullParserException {
		PomRemoveDependency uut = new PomRemoveDependency("com.test", "not-present").relative("pom.xml");

		assertNull(getDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
		assertExceptionOccurred(executionResult);

		assertNotChangedFile("pom.xml");
	}

	@Test
	public void failNotPresentTest() throws IOException, XmlPullParserException {
		PomRemoveDependency uut = new PomRemoveDependency("com.test", "not-present").relative("pom.xml").failIfNotPresent();

		assertNull(getDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
		assertExceptionOccurred(executionResult);

		assertNotChangedFile("pom.xml");
	}

	@Test
	public void warnNotPresentTest() throws IOException, XmlPullParserException {
		PomRemoveDependency uut = new PomRemoveDependency("com.test", "not-present").relative("pom.xml").warnIfNotPresent();

		assertNull(getDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);

		assertNull(executionResult.getException());
		assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
		assertEquals(executionResult.getWarnings().get(0).getMessage(), DEPENDENCY_NOT_REMOVED_MSG);

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
	}

	@Test
	public void noOpNotPresentTest() throws IOException, XmlPullParserException {
		PomRemoveDependency uut = new PomRemoveDependency("com.test", "not-present").relative("pom.xml").noOpIfNotPresent();

		assertNull(getDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
		assertNull(executionResult.getException());
		assertEquals(executionResult.getDetails(), DEPENDENCY_NOT_REMOVED_MSG);

		assertNotChangedFile("pom.xml");
	}

	@Test
	public void getDescriptionTest() throws IOException, XmlPullParserException {
		PomRemoveDependency uut = new PomRemoveDependency("org.testng", "testng").relative("pom.xml");

		String description = uut.getDescription();
		assertEquals(description, "Remove dependency org.testng:testng from POM file pom.xml");
	}

	private Dependency getDependencyBeforeChange(PomRemoveDependency pomRemoveDependency) throws IOException, XmlPullParserException {
		Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
		Dependency dependencyBeforeChange = pomRemoveDependency.getDependency(pomModelBeforeChange);
		return dependencyBeforeChange;
	}

	private Dependency getDependencyAfterChange(PomRemoveDependency pomRemoveDependency) throws IOException, XmlPullParserException {
		Model pomModelAfterChange = getTransformedPomModel("pom.xml");
		Dependency dependencyAfterChange = pomRemoveDependency.getDependency(pomModelAfterChange);
		return dependencyAfterChange;
	}

	private void executeAndAssertSuccess(AbstractPomOperation<?> pomOperation) {
		TOExecutionResult executionResult = pomOperation.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
		assertEquals(executionResult.getDetails(),
				"Dependency org.springframework.boot:spring-boot-dependencies has been removed from POM file /pom.xml");
	}

	private void assertExceptionOccurred(TOExecutionResult executionResult) {
		assertNotNull(executionResult.getException());
		assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
		assertEquals(executionResult.getException().getMessage(), DEPENDENCY_NOT_REMOVED_MSG);
	}

}
