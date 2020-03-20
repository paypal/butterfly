package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.utilities.Log;
import org.slf4j.event.Level;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit tests for {@link TransformationTemplate}
 *
 * @author facarvalho
 */
public class TransformationTemplateTest extends TestHelper {

    @Test
    public void basicTest() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();

        assertEquals(transformationTemplate.getName(), "NO_EXTENSION:AnonymousTransformationTemplate");
        assertEquals(transformationTemplate.toString(), "NO_EXTENSION:AnonymousTransformationTemplate");
        assertEquals(transformationTemplate.getDescription(), "Test transformation template");
        assertNull(transformationTemplate.getApplicationName());
        assertNull(transformationTemplate.getApplicationType());
        assertEquals(transformationTemplate.getChildren().size(), 0);
        assertEquals(transformationTemplate.getUtilities().size(), 0);
    }

    private static final class SampleTransformationTemplate extends TransformationTemplate {
        @Override
        public Class<? extends Extension> getExtensionClass() {return null;}
        @Override
        public String getDescription() {return null;}
    }

    @Test
    public void simpleClassNameTest() {
        assertEquals(getNewTestTransformationTemplate().getSimpleClassName(), "AnonymousTransformationTemplate");
        assertEquals(new SampleTransformationTemplate().getSimpleClassName(), "SampleTransformationTemplate");
    }

    @Test
    public void addTest() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        TransformationUtility transformationUtility = getNewTestTransformationUtility();

        String tuName = transformationTemplate.add(transformationUtility);
        assertEquals(tuName, "NO_EXTENSION:AnonymousTransformationTemplate-1-AnonymousTransformationUtility");
        assertEquals(transformationUtility.getName(), tuName);

        assertTrue(transformationTemplate.getUtilities().contains(transformationUtility));
        assertEquals(transformationTemplate.getUtilities().size(), 1);
    }

    @Test
    public void addUtilityThatAlreadyHasParentTest() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        TransformationUtility transformationUtility = getNewTestTransformationUtility();

        assertEquals(transformationTemplate.add(transformationUtility), "NO_EXTENSION:AnonymousTransformationTemplate-1-AnonymousTransformationUtility");

        try {
            transformationTemplate.add(transformationUtility);
            fail("TransformationDefinitionException was supposed to be thrown");
        } catch (TransformationDefinitionException ex) {
            assertEquals("Invalid attempt to add already registered transformation utility NO_EXTENSION:AnonymousTransformationTemplate-1-AnonymousTransformationUtility to transformation template NO_EXTENSION:AnonymousTransformationTemplate", ex.getMessage());
        }
    }

    @Test
    public void addUtilityWithNameAlreadyRegisteredTest() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        TransformationUtility transformationUtility = getNewTestTransformationUtility();
        transformationTemplate.add(transformationUtility);

        try {
            transformationTemplate.add(getNewTestTransformationUtility().setName("NO_EXTENSION:AnonymousTransformationTemplate-1-AnonymousTransformationUtility"));
            fail("TransformationDefinitionException was supposed to be thrown");
        } catch (TransformationDefinitionException ex) {
            assertEquals("Invalid attempt to add transformation utility NO_EXTENSION:AnonymousTransformationTemplate-1-AnonymousTransformationUtility to transformation template NO_EXTENSION:AnonymousTransformationTemplate. Its name is already registered", ex.getMessage());
        }
    }

    @Test
    public void addUtilityNoFileSetTest() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        TransformationUtility transformationUtility = getNewTestTransformationUtility().relative(null);

        try {
            transformationTemplate.add(transformationUtility);
            fail("TransformationDefinitionException was supposed to be thrown");
        } catch (TransformationDefinitionException ex) {
            assertEquals("Neither absolute, nor relative path, have been set for this transformation utility", ex.getMessage());
        }

        assertEquals(transformationTemplate.getUtilities().size(), 0);

        try {
            transformationTemplate.add(transformationUtility, "foo");
            fail("TransformationDefinitionException was supposed to be thrown");
        } catch (TransformationDefinitionException ex) {
            assertEquals("Neither absolute, nor relative path, have been set for transformation utility foo", ex.getMessage());
        }

        assertEquals(transformationTemplate.getUtilities().size(), 0);
    }

    @Test
    public void addMultipleTest() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        TransformationOperation<TransformationOperation> transformationOperation = getNewTestTransformationOperation();

        assertEquals(transformationTemplate.addMultiple(transformationOperation), "NO_EXTENSION:AnonymousTransformationTemplate-1-MultipleOperations");
        assertEquals(transformationTemplate.getUtilities().size(), 1);

        MultipleOperations multipleOperations = (MultipleOperations) transformationTemplate.getUtilities().get(0);
        assertEquals(multipleOperations.getTemplateOperation(), transformationOperation);
        assertEquals(multipleOperations.getFilesAttributes().length, 0);
        assertNotNull(multipleOperations.getChildren());
        assertEquals(multipleOperations.getChildren().size(), 0);

        assertEquals(transformationTemplate.addMultiple(transformationOperation, "ATT"), "NO_EXTENSION:AnonymousTransformationTemplate-2-MultipleOperations");
        assertEquals(transformationTemplate.getUtilities().size(), 2);

        MultipleOperations multipleOperations2 = (MultipleOperations) transformationTemplate.getUtilities().get(1);
        assertEquals(multipleOperations2.getTemplateOperation(), transformationOperation);
        assertEquals(multipleOperations2.getFilesAttributes().length, 1);
        assertNotNull(multipleOperations2.getChildren());
        assertEquals(multipleOperations2.getChildren().size(), 0);
    }

    @Test
    public void logTest1() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        transformationTemplate.log("test");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        Log log = (Log) transformationTemplate.getUtilities().get(0);

        assertEquals(log.getLogLevel(), Level.INFO);
        assertEquals(log.getLogMessage(), "test");
        assertNull(log.getAttributeNames());
    }

    @Test
    public void logTest2() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        transformationTemplate.log(Level.WARN, "test");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        Log log = (Log) transformationTemplate.getUtilities().get(0);

        assertEquals(log.getLogLevel(), Level.WARN);
        assertEquals(log.getLogMessage(), "test");
        assertNull(log.getAttributeNames());
    }

    @Test
    public void logTest3() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        transformationTemplate.log("test {}", "ATT");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        Log log = (Log) transformationTemplate.getUtilities().get(0);

        assertEquals(log.getLogLevel(), Level.INFO);
        assertEquals(log.getLogMessage(), "test {}");
        assertEquals(log.getAttributeNames(), new String[]{"ATT"});
    }

    @Test
    public void logTest4() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        transformationTemplate.log(Level.ERROR,"test {}", "ATT");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        Log log = (Log) transformationTemplate.getUtilities().get(0);

        assertEquals(log.getLogLevel(), Level.ERROR);
        assertEquals(log.getLogMessage(), "test {}");
        assertEquals(log.getAttributeNames(), new String[]{"ATT"});
    }

    @Test
    public void infoTest1() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        transformationTemplate.info("test");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        Log log = (Log) transformationTemplate.getUtilities().get(0);

        assertEquals(log.getLogLevel(), Level.INFO);
        assertEquals(log.getLogMessage(), "test");
        assertNull(log.getAttributeNames());
    }

    @Test
    public void infoTest2() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        transformationTemplate.info("test {}", "ATT");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        Log log = (Log) transformationTemplate.getUtilities().get(0);

        assertEquals(log.getLogLevel(), Level.INFO);
        assertEquals(log.getLogMessage(), "test {}");
        assertEquals(log.getAttributeNames(), new String[]{"ATT"});
    }

    @Test
    public void debugTest1() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        transformationTemplate.debug("test");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        Log log = (Log) transformationTemplate.getUtilities().get(0);

        assertEquals(log.getLogLevel(), Level.DEBUG);
        assertEquals(log.getLogMessage(), "test");
        assertNull(log.getAttributeNames());
    }

    @Test
    public void debugTest2() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        transformationTemplate.debug("test {}", "ATT");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        Log log = (Log) transformationTemplate.getUtilities().get(0);

        assertEquals(log.getLogLevel(), Level.DEBUG);
        assertEquals(log.getLogMessage(), "test {}");
        assertEquals(log.getAttributeNames(), new String[]{"ATT"});
    }

    @Test
    public void loopIterationsTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();

        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        assertEquals(transformationTemplate.loop(transformationUtility, 4), "NO_EXTENSION:AnonymousTransformationTemplate-1-TransformationUtilityLoop");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        TransformationUtilityLoop loop = (TransformationUtilityLoop) transformationTemplate.getUtilities().get(0);
        assertEquals(loop.getTemplate(), transformationUtility);
        assertEquals(loop.getIterations(), 4);
        assertNull(loop.getAttribute());
        assertNull(loop.getCondition());
    }

    @Test
    public void loopAttributeTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();

        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        assertEquals(transformationTemplate.loop(transformationUtility, "ATT"), "NO_EXTENSION:AnonymousTransformationTemplate-1-TransformationUtilityLoop");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        TransformationUtilityLoop loop = (TransformationUtilityLoop) transformationTemplate.getUtilities().get(0);
        assertEquals(loop.getTemplate(), transformationUtility);
        assertEquals(loop.getIterations(), -1);
        assertEquals(loop.getAttribute(), "ATT");
        assertNull(loop.getCondition());
    }

    @Test
    public void loopConditionTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();
        UtilityCondition utilityCondition = getNewTestUtilityCondition(true);

        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        assertEquals(transformationTemplate.getUtilities().size(), 0);
        assertEquals(transformationTemplate.loop(transformationUtility, utilityCondition), "NO_EXTENSION:AnonymousTransformationTemplate-1-TransformationUtilityLoop");
        assertEquals(transformationTemplate.getUtilities().size(), 1);
        TransformationUtilityLoop loop = (TransformationUtilityLoop) transformationTemplate.getUtilities().get(0);
        assertEquals(loop.getTemplate(), transformationUtility);
        assertEquals(loop.getIterations(), -1);
        assertNull(loop.getAttribute());
        assertEquals(loop.getCondition(), utilityCondition);
    }

}
