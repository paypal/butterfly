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

/**
 * Unit test class for {@link PomRemoveManagedDependency}
 *
 * @author facarvalho
 */
public class PomRemoveManagedDependencyTest extends TransformationUtilityTestHelper {

	private static final String MANAGED_DEPENDENCY_NOT_REMOVED_MSG = "Managed dependency com.test:not-present has NOT been removed from POM file /pom.xml because it is not present";

	@Test
	public void miscTest() {
		PomRemoveManagedDependency pomRemoveManagedDependency = new PomRemoveManagedDependency("junit", "junit").relative("pom.xml");

		assertEquals(pomRemoveManagedDependency.getDescription(), "Remove managed dependency junit:junit from POM file pom.xml");
		assertEquals(pomRemoveManagedDependency.clone(), pomRemoveManagedDependency);
	}

	@Test
	public void removeDependencyTest() throws IOException, XmlPullParserException {
		PomRemoveManagedDependency uut = new PomRemoveManagedDependency("junit", "junit").relative("pom.xml");

		assertNotNull(getManagedDependencyBeforeChange(uut));
		executeAndAssertSuccess(uut);
		Dependency dependencyAfterChange = getDependencyAfterChange(uut);
		assertNull(dependencyAfterChange);
	}

	@Test
	public void defaultNotPresentTest() throws IOException, XmlPullParserException {
		PomRemoveManagedDependency uut = new PomRemoveManagedDependency("com.test", "not-present").relative("pom.xml");

		assertNull(getManagedDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
		assertExceptionOccurred(executionResult);

		assertNotChangedFile("pom.xml");
	}

	@Test
	public void failNotPresentTest() throws IOException, XmlPullParserException {
		PomRemoveManagedDependency uut = new PomRemoveManagedDependency("com.test", "not-present").relative("pom.xml").failIfNotPresent();

		assertNull(getManagedDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
		assertExceptionOccurred(executionResult);

		assertNotChangedFile("pom.xml");
	}

	@Test
	public void warnNotPresentTest() throws IOException, XmlPullParserException {
		PomRemoveManagedDependency uut = new PomRemoveManagedDependency("com.test", "not-present").relative("pom.xml").warnIfNotPresent();

		assertNull(getManagedDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);

		assertNull(executionResult.getException());
		assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
		assertEquals(executionResult.getWarnings().get(0).getMessage(), MANAGED_DEPENDENCY_NOT_REMOVED_MSG);

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
	}

	@Test
	public void noOpNotPresentTest() throws IOException, XmlPullParserException {
		PomRemoveManagedDependency uut = new PomRemoveManagedDependency("com.test", "not-present").relative("pom.xml").noOpIfNotPresent();

		assertNull(getManagedDependencyBeforeChange(uut));
		TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
		assertNull(executionResult.getException());
		assertEquals(executionResult.getDetails(), MANAGED_DEPENDENCY_NOT_REMOVED_MSG);

		assertNotChangedFile("pom.xml");
	}

	@Test
	public void getDescriptionTest() {
		PomRemoveManagedDependency uut = new PomRemoveManagedDependency("org.testng", "testng").relative("pom.xml");

		String description = uut.getDescription();
		assertEquals(description, "Remove managed dependency org.testng:testng from POM file pom.xml");
	}

	private Dependency getManagedDependencyBeforeChange(PomRemoveManagedDependency pomRemoveManagedDependency) throws IOException, XmlPullParserException {
		Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
		return pomRemoveManagedDependency.getManagedDependency(pomModelBeforeChange);
	}

	private Dependency getDependencyAfterChange(PomRemoveManagedDependency pomRemoveManagedDependency) throws IOException, XmlPullParserException {
		Model pomModelAfterChange = getTransformedPomModel("pom.xml");
		return pomRemoveManagedDependency.getManagedDependency(pomModelAfterChange);
	}

	private void executeAndAssertSuccess(AbstractPomOperation<?> pomOperation) {
		TOExecutionResult executionResult = pomOperation.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
		assertEquals(executionResult.getDetails(), "Managed dependency junit:junit has been removed from POM file /pom.xml");
	}

	private void assertExceptionOccurred(TOExecutionResult executionResult) {
		assertNotNull(executionResult.getException());
		assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
		assertEquals(executionResult.getException().getMessage(), MANAGED_DEPENDENCY_NOT_REMOVED_MSG);
	}

}
