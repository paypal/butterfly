package com.paypal.butterfly.utilities.operations.pom;

import com.google.common.io.Files;
import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Unit test class for {@link PomAddParent}
 *
 * @author facarvalho
 */
public class PomAddParentTest extends TransformationUtilityTestHelper {

    @Test
    public void addParentTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelBeforeChange.getParent());

        PomAddParent pomAddParent = new PomAddParent().setGroupId("com.newgroupid").setArtifactId("newartifactid").setVersion("2.0").relative("/src/main/resources/no_parent_pom.xml");

        assertEquals(pomAddParent.getGroupId(), "com.newgroupid");
        assertEquals(pomAddParent.getArtifactId(), "newartifactid");
        assertEquals(pomAddParent.getVersion(), "2.0");

        TOExecutionResult executionResult = pomAddParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(pomAddParent.getDescription(), "Add parent artifact com.newgroupid:newartifactid in POM file /src/main/resources/no_parent_pom.xml");
        assertEquals(executionResult.getDetails(), "Parent for POM file /src/main/resources/no_parent_pom.xml has been set to com.newgroupid:newartifactid:pom:2.0");
        assertNull(executionResult.getException());

        Model pomModelAfterChange = getTransformedPomModel("/src/main/resources/no_parent_pom.xml");
        assertEquals(pomModelAfterChange.getParent().getGroupId(), "com.newgroupid");
        assertEquals(pomModelAfterChange.getParent().getArtifactId(), "newartifactid");
        assertEquals(pomModelAfterChange.getParent().getVersion(), "2.0");

        // Checking indentation
        File baselineFile = new File(this.getClass().getResource("/test-app/src/main/resources/added_parent_pom.xml").getFile());
        assertTrue(Files.equal(baselineFile, new File(transformedAppFolder, "/src/main/resources/no_parent_pom.xml")), "File is not formatted properly.");
    }

    @Test
    public void replaceParentTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getParent().getGroupId(), "com.test");
        assertEquals(pomModelBeforeChange.getParent().getArtifactId(), "foo-parent");
        assertEquals(pomModelBeforeChange.getParent().getVersion(), "1.0");

        PomAddParent pomAddParent = new PomAddParent().setGroupId("com.newgroupid").setArtifactId("newartifactid").setVersion("2.0").relative("pom.xml").overwriteIfPresent();

        assertEquals(pomAddParent.getGroupId(), "com.newgroupid");
        assertEquals(pomAddParent.getArtifactId(), "newartifactid");
        assertEquals(pomAddParent.getVersion(), "2.0");

        TOExecutionResult executionResult = pomAddParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(pomAddParent.getDescription(), "Add parent artifact com.newgroupid:newartifactid in POM file pom.xml");
        assertNull(executionResult.getException());

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getParent().getGroupId(), "com.newgroupid");
        assertEquals(pomModelAfterChange.getParent().getArtifactId(), "newartifactid");
        assertEquals(pomModelAfterChange.getParent().getVersion(), "2.0");

        // Checking indentation
        File baselineFile = new File(this.getClass().getResource("/test-app/src/main/resources/replaced_parent_pom.xml").getFile());
        assertTrue(Files.equal(baselineFile, new File(transformedAppFolder, "pom.xml")), "File is not formatted properly.");
    }

    @Test
    public void failTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getParent().getGroupId(), "com.test");
        assertEquals(pomModelBeforeChange.getParent().getArtifactId(), "foo-parent");
        assertEquals(pomModelBeforeChange.getParent().getVersion(), "1.0");

        PomAddParent pomAddParent = new PomAddParent().setGroupId("com.newgroupid").setArtifactId("newartifactid").setVersion("2.0").relative("pom.xml").failIfPresent();

        assertEquals(pomAddParent.getGroupId(), "com.newgroupid");
        assertEquals(pomAddParent.getArtifactId(), "newartifactid");
        assertEquals(pomAddParent.getVersion(), "2.0");

        TOExecutionResult executionResult = pomAddParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(pomAddParent.getDescription(), "Add parent artifact com.newgroupid:newartifactid in POM file pom.xml");
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Pom file /pom.xml already has a parent");

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getParent().getGroupId(), "com.test");
        assertEquals(pomModelAfterChange.getParent().getArtifactId(), "foo-parent");
        assertEquals(pomModelAfterChange.getParent().getVersion(), "1.0");

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void noOpTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getParent().getGroupId(), "com.test");
        assertEquals(pomModelBeforeChange.getParent().getArtifactId(), "foo-parent");
        assertEquals(pomModelBeforeChange.getParent().getVersion(), "1.0");

        PomAddParent pomAddParent = new PomAddParent().setGroupId("com.newgroupid").setArtifactId("newartifactid").setVersion("2.0").relative("pom.xml").noOpIfPresent();

        assertEquals(pomAddParent.getGroupId(), "com.newgroupid");
        assertEquals(pomAddParent.getArtifactId(), "newartifactid");
        assertEquals(pomAddParent.getVersion(), "2.0");

        TOExecutionResult executionResult = pomAddParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertEquals(pomAddParent.getDescription(), "Add parent artifact com.newgroupid:newartifactid in POM file pom.xml");
        assertNull(executionResult.getException());
        assertEquals(executionResult.getDetails(), "Pom file /pom.xml already has a parent");

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getParent().getGroupId(), "com.test");
        assertEquals(pomModelAfterChange.getParent().getArtifactId(), "foo-parent");
        assertEquals(pomModelAfterChange.getParent().getVersion(), "1.0");

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void warnNotAddTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getParent().getGroupId(), "com.test");
        assertEquals(pomModelBeforeChange.getParent().getArtifactId(), "foo-parent");
        assertEquals(pomModelBeforeChange.getParent().getVersion(), "1.0");

        PomAddParent pomAddParent = new PomAddParent().setGroupId("com.newgroupid").setArtifactId("newartifactid").setVersion("2.0").relative("pom.xml").warnNotAddIfPresent();

        assertEquals(pomAddParent.getGroupId(), "com.newgroupid");
        assertEquals(pomAddParent.getArtifactId(), "newartifactid");
        assertEquals(pomAddParent.getVersion(), "2.0");

        TOExecutionResult executionResult = pomAddParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertEquals(pomAddParent.getDescription(), "Add parent artifact com.newgroupid:newartifactid in POM file pom.xml");
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().size(), 0);
        assertEquals(executionResult.getDetails(), "Pom file /pom.xml already has a parent");

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getParent().getGroupId(), "com.test");
        assertEquals(pomModelAfterChange.getParent().getArtifactId(), "foo-parent");
        assertEquals(pomModelAfterChange.getParent().getVersion(), "1.0");

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void warnButAddTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getParent().getGroupId(), "com.test");
        assertEquals(pomModelBeforeChange.getParent().getArtifactId(), "foo-parent");
        assertEquals(pomModelBeforeChange.getParent().getVersion(), "1.0");

        PomAddParent pomAddParent = new PomAddParent("com.newgroupid", "newartifactid","2.0").relative("pom.xml").warnButAddIfPresent();

        assertEquals(pomAddParent.getGroupId(), "com.newgroupid");
        assertEquals(pomAddParent.getArtifactId(), "newartifactid");
        assertEquals(pomAddParent.getVersion(), "2.0");

        TOExecutionResult executionResult = pomAddParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertEquals(pomAddParent.getDescription(), "Add parent artifact com.newgroupid:newartifactid in POM file pom.xml");
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().size(), 0);
        assertEquals(executionResult.getDetails(), "Parent for POM file /pom.xml has been overwritten to com.newgroupid:newartifactid:pom:2.0");

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getParent().getGroupId(), "com.newgroupid");
        assertEquals(pomModelAfterChange.getParent().getArtifactId(), "newartifactid");
        assertEquals(pomModelAfterChange.getParent().getVersion(), "2.0");

        assertChangedFile("pom.xml");

        // Checking indentation
        File baselineFile = new File(this.getClass().getResource("/test-app/src/main/resources/replaced_parent_pom.xml").getFile());
        assertTrue(Files.equal(baselineFile, new File(transformedAppFolder, "pom.xml")), "File is not formatted properly.");
    }

}
