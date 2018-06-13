package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class TransformationUtilityTest extends TransformationUtilityTestHelper {

    // TODO
    //
    // applyPropertiesFromContext
    // perform
    // clone

    @Test
    public void basicTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();

        assertEquals(transformationUtility.getDescription(), "Test transformation utility");
        assertEquals(transformationUtility.getDescription(), transformationUtility.toString());
        assertFalse(transformationUtility.hasBeenPerformed());
        assertNull(transformationUtility.getAbortionMessage());
        assertNull(transformationUtility.getName());
        assertNull(transformationUtility.getContextAttributeName());
        assertNull(transformationUtility.getIfConditionAttributeName());
        assertEquals(transformationUtility.getOrder(), -1);
        assertNull(transformationUtility.getParent());
        assertNull(transformationUtility.getTransformationTemplate());
        assertNull(transformationUtility.getUnlessConditionAttributeName());
        assertTrue(transformationUtility.isSaveResult());
        assertTrue(transformationUtility.isFileSet());
        assertFalse(transformationUtility.wasFileExplicitlySet());
        assertFalse(transformationUtility.isAbortOnFailure());

        transformationUtility.setSaveResult(false);
        assertFalse(transformationUtility.isSaveResult());

        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();

        transformationTemplate.add(transformationUtility);

        assertFalse(transformationUtility.hasBeenPerformed());
        assertEquals(transformationUtility.getName(), "NO_EXTENSION:AnonymousTransformationTemplate-1-AnonymousTransformationUtility");
        assertEquals(transformationUtility.getContextAttributeName(), "NO_EXTENSION:AnonymousTransformationTemplate-1-AnonymousTransformationUtility");
        assertEquals(transformationUtility.getOrder(), 1);
        assertEquals(transformationUtility.getParent(), transformationTemplate);
        assertEquals(transformationUtility.getTransformationTemplate(), transformationTemplate);
    }

    @Test
    public void getTransformationTemplateTest() {
        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        TransformationUtilityGroup transformationUtilityGroup = new TransformationUtilityGroup();
        transformationTemplate.add(transformationUtilityGroup);

        TransformationUtility transformationUtility = getNewTestTransformationUtility();
        transformationUtilityGroup.add(transformationUtility);

        assertEquals(transformationUtility.getParent(), transformationUtilityGroup);
        assertEquals(transformationUtility.getTransformationTemplate(), transformationTemplate);
    }

    // TODO
    // Once issue #80 is solved, this unit test should be changed accordingly
    // https://github.com/paypal/butterfly/issues/80
    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Invalid attempt to add transformation utility to utilities group. This group has to be added to a transformation utilities parent first. Add it to another group, or to a transformation template.")
    public void addingTransformationUtilityToGroupFailTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();
        TransformationUtilityGroup transformationUtilityGroup = new TransformationUtilityGroup();
        transformationUtilityGroup.add(transformationUtility);
    }

    @Test
    public void checkForBlankStringTest() {
        TransformationUtility.checkForBlankString("color", "blue");
    }

    @Test
    public void checkForEmptyStringTest() {
        TransformationUtility.checkForEmptyString("color", null);
    }

    @Test
    public void checkForNullTest() {
        TransformationUtility.checkForNull("number", new Integer(3));
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "color cannot be blank")
    public void checkForBlankStringFailTest1() {
        TransformationUtility.checkForBlankString("color", "");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "color cannot be blank")
    public void checkForBlankStringFailTest2() {
        TransformationUtility.checkForBlankString("color", null);
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "color cannot be empty")
    public void checkForEmptyStringFailTest() {
        TransformationUtility.checkForEmptyString("color", "");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "number cannot be null")
    public void checkForNullFailTest() {
        TransformationUtility.checkForNull("number", null);
    }

    @Test
    public void transformationContextNameTest() {
        TransformationUtility transformationUtility1 = getNewTestTransformationUtility();
        assertNull(transformationUtility1.getContextAttributeName());

        TransformationTemplate transformationTemplate = getNewTestTransformationTemplate();
        transformationTemplate.add(transformationUtility1);
        assertEquals(transformationUtility1.getName(), "NO_EXTENSION:AnonymousTransformationTemplate-1-AnonymousTransformationUtility");
        assertEquals(transformationUtility1.getContextAttributeName(), transformationUtility1.getName());

        assertEquals(transformationUtility1.setContextAttributeName("CUSTOM_CONTEXT_ATTRIBUTE_NAME_1"), transformationUtility1);
        assertEquals(transformationUtility1.getContextAttributeName(), "CUSTOM_CONTEXT_ATTRIBUTE_NAME_1");

        assertEquals(transformationUtility1.setName("NEW_TU_NAME"), transformationUtility1);
        assertEquals(transformationUtility1.getContextAttributeName(), "CUSTOM_CONTEXT_ATTRIBUTE_NAME_1");

        TransformationUtility transformationUtility2 = getNewTestTransformationUtility();
        assertEquals(transformationUtility2.setName("CUSTOM_CONTEXT_ATTRIBUTE_NAME_2"), transformationUtility2);

        assertEquals(transformationUtility2.getName(), "CUSTOM_CONTEXT_ATTRIBUTE_NAME_2");

        transformationTemplate.add(transformationUtility2);

        assertEquals(transformationUtility2.getName(), "CUSTOM_CONTEXT_ATTRIBUTE_NAME_2");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Transformation utility name cannot be blank")
    public void blankNameTest() {
        getNewTestTransformationUtility().setName(" ");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Transformation utility name cannot be blank")
    public void nullNameTest() {
        getNewTestTransformationUtility().setName(null);
    }

    @Test
    public void relativeTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();

        assertEquals(transformationUtility.relative("pom.xml"), transformationUtility);
        assertTrue(transformationUtility.wasFileExplicitlySet());
        assertTrue(transformationUtility.isFileSet());

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);

        TUExecutionResult executionResult = (TUExecutionResult) performResult.getExecutionResult();
        assertEquals(executionResult.getValue(), new File(transformedAppFolder, "pom.xml"));
    }

    @Test
    public void nullRelativeTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();
        assertEquals(transformationUtility.setName("TU"), transformationUtility);

        assertEquals(transformationUtility.relative(null), transformationUtility);
        assertFalse(transformationUtility.wasFileExplicitlySet());
        assertFalse(transformationUtility.isFileSet());

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.ERROR);
        assertTrue(performResult.isExceptionType());
        assertEquals(performResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getMessage(), "Utility TU has failed");
        assertEquals(performResult.getException().getCause().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getCause().getMessage(), "Neither absolute nor relative path has been set for transformation utility TU");
    }

    @Test
    public void absoluteTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();

        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(new File(transformedAppFolder, "pom.xml"));
        assertEquals(transformationUtility.absolute("ATT"), transformationUtility);
        assertTrue(transformationUtility.wasFileExplicitlySet());
        assertTrue(transformationUtility.isFileSet());

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);

        TUExecutionResult executionResult = (TUExecutionResult) performResult.getExecutionResult();
        assertEquals(executionResult.getValue(), new File(transformedAppFolder, "pom.xml"));
    }

    @Test
    public void absoluteAdditionalPathTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();

        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(transformedAppFolder);
        assertEquals(transformationUtility.absolute("ATT", "pom.xml"), transformationUtility);
        assertTrue(transformationUtility.wasFileExplicitlySet());
        assertTrue(transformationUtility.isFileSet());

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);

        TUExecutionResult executionResult = (TUExecutionResult) performResult.getExecutionResult();
        assertEquals(executionResult.getValue(), new File(transformedAppFolder, "pom.xml"));
    }

    @Test
    public void absoluteNonexistentContextAttributeTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();
        assertEquals(transformationUtility.setName("TU"), transformationUtility);

        assertEquals(transformationUtility.absolute("ATT"), transformationUtility);
        assertTrue(transformationUtility.wasFileExplicitlySet());
        assertTrue(transformationUtility.isFileSet());

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.ERROR);
        assertTrue(performResult.isExceptionType());
        assertEquals(performResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getMessage(), "Utility TU has failed");
        assertEquals(performResult.getException().getCause().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getCause().getMessage(), "Context attribute ATT, which is supposed to define absolute file for TU, does not exist");
    }

    @Test
    public void absoluteNullContextAttributeTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();
        assertEquals(transformationUtility.setName("TU"), transformationUtility);

        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(null);
        assertEquals(transformationUtility.absolute("ATT"), transformationUtility);
        assertTrue(transformationUtility.wasFileExplicitlySet());
        assertTrue(transformationUtility.isFileSet());

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.ERROR);
        assertTrue(performResult.isExceptionType());
        assertEquals(performResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getMessage(), "Utility TU has failed");
        assertEquals(performResult.getException().getCause().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getCause().getMessage(), "Context attribute ATT, which is supposed to define absolute file for TU, is null");
    }

    @Test
    public void alreadyPerformedTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility().setName("TU");

        assertEquals(transformationUtility.relative("pom.xml"), transformationUtility);

        // Performing it first
        assertFalse(transformationUtility.hasBeenPerformed());
        transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        // Performing it again
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.ERROR);
        assertTrue(performResult.isExceptionType());
        assertEquals(performResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getMessage(), "Utility TU has already been performed");
        assertNull(performResult.getException().getCause());
    }

    @Test
    public void abortOnFailureTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility();
        assertFalse(transformationUtility.isAbortOnFailure());
        assertNull(transformationUtility.getAbortionMessage());

        assertEquals(transformationUtility.abortOnFailure(true), transformationUtility);
        assertTrue(transformationUtility.isAbortOnFailure());
        assertEquals(transformationUtility.abortOnFailure(false), transformationUtility);
        assertFalse(transformationUtility.isAbortOnFailure());
        assertEquals(transformationUtility.abortOnFailure("Let's crash it!"), transformationUtility);
        assertTrue(transformationUtility.isAbortOnFailure());
        assertEquals(transformationUtility.getAbortionMessage(), "Let's crash it!");
        assertEquals(transformationUtility.abortOnFailure(false), transformationUtility);
        assertNull(transformationUtility.getAbortionMessage());
    }

    @Test
    public void runtimePropertyTest() {
        TransformationUtility transformationUtility = new TransformationUtility() {

            private String color;

            public TransformationUtility setColor(String color) {
                this.color = color;
                return this;
            }

            public String getColor() {
                return color;
            }

            @Override
            public String getDescription() {
                return "Test transformation utility";
            }
            @Override
            protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
                return TUExecutionResult.value(this, color);
            }
        };

        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn("blue");

        assertEquals(transformationUtility.set("color", "ATT"), transformationUtility);

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);

        TUExecutionResult executionResult = (TUExecutionResult) performResult.getExecutionResult();
        assertEquals(executionResult.getValue(), "blue");
    }

    @Test
    public void nullRuntimePropertyTest() {
        TransformationUtility transformationUtility = new TransformationUtility() {

            private String color;

            public TransformationUtility setColor(String color) {
                this.color = color;
                return this;
            }

            public String getColor() {
                return color;
            }

            @Override
            public String getDescription() {
                return "Test transformation utility";
            }
            @Override
            protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
                return TUExecutionResult.value(this, color);
            }
        };

        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(null);

        assertEquals(transformationUtility.set("color", "ATT"), transformationUtility);
        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.NULL);

        TUExecutionResult executionResult = (TUExecutionResult) performResult.getExecutionResult();
        assertNull(executionResult.getValue());
    }

    @Test
    public void nullPrimitiveRuntimePropertyTest() {
        TransformationUtility transformationUtility = new TransformationUtility() {

            private int number;

            public TransformationUtility setNumber(int number) {
                this.number = number;
                return this;
            }

            public int getNumber() {
                return number;
            }

            @Override
            public String getDescription() {
                return "Test transformation utility";
            }
            @Override
            protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
                return TUExecutionResult.value(this, number);
            }
        };

        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(null);

        assertEquals(transformationUtility.setName("TU"), transformationUtility);
        assertEquals(transformationUtility.set("number", "ATT"), transformationUtility);

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.ERROR);
        assertTrue(performResult.isExceptionType());
        assertEquals(performResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getMessage(), "Utility TU has failed");
        assertEquals(performResult.getException().getCause().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getCause().getMessage(), "An error happened when setting property 'number' from context attribute 'ATT' in 'TU'");
        assertEquals(performResult.getException().getCause().getCause().getClass(), IllegalArgumentException.class);
        assertNull(performResult.getException().getCause().getCause().getMessage());
    }

    @Test
    public void longToIntegerRuntimePropertyTest() {
        TransformationUtility transformationUtility = new TransformationUtility() {

            private Integer number;

            public TransformationUtility setNumber(Integer number) {
                this.number = number;
                return this;
            }

            public Integer getNumber() {
                return number;
            }

            @Override
            public String getDescription() {
                return "Test transformation utility";
            }
            @Override
            protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
                return TUExecutionResult.value(this, number);
            }
        };

        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(new Long(12));

        assertEquals(transformationUtility.setName("TU"), transformationUtility);
        assertEquals(transformationUtility.set("number", "ATT"), transformationUtility);

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);

        TUExecutionResult executionResult = (TUExecutionResult) performResult.getExecutionResult();
        assertEquals(executionResult.getValue(), 12);
    }

    @Test
    public void longToShortRuntimePropertyTest() {
        TransformationUtility transformationUtility = new TransformationUtility() {

            private Short number;

            public TransformationUtility setNumber(Short number) {
                this.number = number;
                return this;
            }

            public Short getNumber() {
                return number;
            }

            @Override
            public String getDescription() {
                return "Test transformation utility";
            }
            @Override
            protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
                return TUExecutionResult.value(this, number);
            }
        };

        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(new Long(12));

        assertEquals(transformationUtility.setName("TU"), transformationUtility);
        assertEquals(transformationUtility.set("number", "ATT"), transformationUtility);

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);

        TUExecutionResult executionResult = (TUExecutionResult) performResult.getExecutionResult();
        assertEquals(executionResult.getValue(), new Short("12"));
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "color is not a valid property")
    public void invalidRuntimePropertyTest() {
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn("blue");

        getNewTestTransformationUtility().set("color", "ATT");
    }

    @Test
    public void invalidAttributeRuntimePropertyTest() {
        TransformationUtility transformationUtility = new TransformationUtility() {

            private String color;

            public TransformationUtility setColor(String color) {
                this.color = color;
                return this;
            }

            public String getColor() {
                return color;
            }

            @Override
            public String getDescription() {
                return "Test transformation utility";
            }
            @Override
            protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
                return TUExecutionResult.value(this, color);
            }
        };

        assertEquals(transformationUtility.setName("TU"), transformationUtility);
        assertEquals(transformationUtility.set("color", "ATT"), transformationUtility);

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.ERROR);
        assertTrue(performResult.isExceptionType());
        assertEquals(performResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getMessage(), "Utility TU has failed");
        assertEquals(performResult.getException().getCause().getClass(), TransformationUtilityException.class);
        assertEquals(performResult.getException().getCause().getMessage(), "Attempt to set property 'color' for 'TU' failed, there is no transformation context attribute named 'ATT'");
    }

    @Test
    public void dependsOnTest() {
        TransformationUtility tuA = getNewTestTransformationUtility().setName("A");
        TransformationUtility tuB = getNewTestTransformationUtility().setName("B");
        tuB.dependsOn("A");

        assertFalse(tuA.hasBeenPerformed());
        PerformResult performResultA = tuA.perform(transformedAppFolder, transformationContext);
        assertTrue(tuA.hasBeenPerformed());

        assertEquals(performResultA.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResultA.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);
        assertEquals(((TUExecutionResult) performResultA.getExecutionResult()).getValue(), transformedAppFolder);

        Mockito.when(transformationContext.getResult("A")).thenReturn(performResultA);

        assertFalse(tuB.hasBeenPerformed());
        PerformResult performResultB = tuB.perform(transformedAppFolder, transformationContext);
        assertTrue(tuB.hasBeenPerformed());

        assertEquals(performResultB.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResultB.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);
        assertEquals(((TUExecutionResult) performResultB.getExecutionResult()).getValue(), transformedAppFolder);
    }

    @Test
    public void notPerformedSkipDependsOnTest() {
        TransformationUtility tuB = getNewTestTransformationUtility().setName("B").dependsOn("A");

        assertFalse(tuB.hasBeenPerformed());
        PerformResult performResultB = tuB.perform(transformedAppFolder, transformationContext);
        assertTrue(tuB.hasBeenPerformed());

        assertEquals(performResultB.getType(), PerformResult.Type.SKIPPED_DEPENDENCY);
        assertEquals(performResultB.getDetails(), "B was skipped because its dependency A has not been executed yet");
        assertFalse(performResultB.isExceptionType());
        assertNull(performResultB.getException());
    }

    @Test
    public void failedSkipDependsOnTest() {
        TransformationUtility tuA = getNewTestTransformationUtility().setName("A").absolute("NON_EXISTENT_ATTRIBUTE");
        TransformationUtility tuB = getNewTestTransformationUtility().setName("B");
        tuB.dependsOn("A");

        assertFalse(tuA.hasBeenPerformed());
        PerformResult performResultA = tuA.perform(transformedAppFolder, transformationContext);
        assertTrue(tuA.hasBeenPerformed());

        assertEquals(performResultA.getType(), PerformResult.Type.ERROR);
        assertEquals(performResultA.getException().getCause().getMessage(), "Context attribute NON_EXISTENT_ATTRIBUTE, which is supposed to define absolute file for A, does not exist");

        Mockito.when(transformationContext.getResult("A")).thenReturn(performResultA);

        assertFalse(tuB.hasBeenPerformed());
        PerformResult performResultB = tuB.perform(transformedAppFolder, transformationContext);
        assertTrue(tuB.hasBeenPerformed());

        assertEquals(performResultB.getType(), PerformResult.Type.SKIPPED_DEPENDENCY);
        assertEquals(performResultB.getDetails(), "B was skipped because its dependency A resulted in ERROR");
        assertFalse(performResultB.isExceptionType());
        assertNull(performResultB.getException());
    }

    @Test
    public void executeIf() {
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(true);

        TransformationUtility transformationUtility = getNewTestTransformationUtility().executeIf("ATT");

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);
        assertEquals(((TUExecutionResult) performResult.getExecutionResult()).getValue(), transformedAppFolder);
    }

    @Test
    public void executeIfSkip() {
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(false);

        TransformationUtility transformationUtility = getNewTestTransformationUtility().setName("TU").executeIf("ATT");

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertEquals(performResult.getType(), PerformResult.Type.SKIPPED_CONDITION);
        assertEquals(performResult.getDetails(), "TU was skipped due to failing 'if' condition: ATT");
        assertFalse(performResult.isExceptionType());
        assertNull(performResult.getException());
    }

    @Test
    public void executeIfSkipNonexistentContextAttributeTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility().setName("TU").executeIf("ATT");

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertEquals(performResult.getType(), PerformResult.Type.SKIPPED_CONDITION);
        assertEquals(performResult.getDetails(), "TU was skipped due to failing 'if' condition: ATT");
        assertFalse(performResult.isExceptionType());
        assertNull(performResult.getException());
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Condition attribute name cannot be blank")
    public void executeIfBlankContextAttributeTest() {
        getNewTestTransformationUtility().executeIf("");
    }

    @Test
    public void executeUnless() {
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(false);

        TransformationUtility transformationUtility = getNewTestTransformationUtility().executeUnless("ATT");

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);
        assertEquals(((TUExecutionResult) performResult.getExecutionResult()).getValue(), transformedAppFolder);
    }

    @Test
    public void executeUnlessSkip() {
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(true);

        TransformationUtility transformationUtility = getNewTestTransformationUtility().setName("TU").executeUnless("ATT");

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertEquals(performResult.getType(), PerformResult.Type.SKIPPED_CONDITION);
        assertEquals(performResult.getDetails(), "TU was skipped due to failing 'unless' condition: ATT");
        assertFalse(performResult.isExceptionType());
        assertNull(performResult.getException());
    }

    @Test
    public void executeUnlessSkipNonexistentContextAttributeTest() {
        TransformationUtility transformationUtility = getNewTestTransformationUtility().setName("TU").executeUnless("ATT");

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);
        assertEquals(((TUExecutionResult) performResult.getExecutionResult()).getValue(), transformedAppFolder);
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Condition attribute name cannot be blank")
    public void executeUnlessBlankContextAttributeTest() {
        getNewTestTransformationUtility().executeUnless("");
    }

    @Test
    public void executeIfObject() {
        UtilityCondition utilityCondition = getNewUtilityCondition(true);
        TransformationUtility transformationUtility = getNewTestTransformationUtility().executeIf(utilityCondition);

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TUExecutionResult.Type.VALUE);
        assertEquals(((TUExecutionResult) performResult.getExecutionResult()).getValue(), transformedAppFolder);
    }

    @Test
    public void executeIfObjectSkip() {
        UtilityCondition utilityCondition = getNewUtilityCondition(false);
        TransformationUtility transformationUtility = getNewTestTransformationUtility().setName("TU").executeIf(utilityCondition);

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertEquals(performResult.getType(), PerformResult.Type.SKIPPED_CONDITION);
        assertEquals(performResult.getDetails(), "TU was skipped due to failing UtilityCondition 'Test utility condition'");
        assertFalse(performResult.isExceptionType());
        assertNull(performResult.getException());
    }

    @Test
    public void executeIfObjectSkipError() {
        UtilityCondition utilityCondition = new UtilityCondition() {
            @Override
            public String getDescription() {
                return "Test utility condition";
            }
            @Override
            protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
                throw new RuntimeException();
            }
        };
        TransformationUtility transformationUtility = getNewTestTransformationUtility().setName("TU").executeIf(utilityCondition);

        assertFalse(transformationUtility.hasBeenPerformed());
        PerformResult performResult = transformationUtility.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationUtility.hasBeenPerformed());

        assertEquals(performResult.getType(), PerformResult.Type.SKIPPED_CONDITION);
        assertEquals(performResult.getDetails(), "TU was skipped due to failing UtilityCondition 'Test utility condition'");
        assertFalse(performResult.isExceptionType());
        assertNull(performResult.getException());
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Utility condition object cannot be null")
    public void executeIfObjectNullContextAttributeTest() {
        getNewTestTransformationUtility().executeIf((UtilityCondition) null);
    }

    private static final class SampleTransformationUtility extends TransformationUtility {
        @Override
        public String getDescription() {return null;}
        @Override
        protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {return null;}
    }

    @Test
    public void getSimpleClassName() {
        TransformationUtility transformationUtility = new SampleTransformationUtility();
        assertEquals(transformationUtility.getSimpleClassName(), "SampleTransformationUtility");
    }

    @Test
    public void hashCodeTest() {
        TransformationUtility tu1 = getNewTestTransformationUtility();
        TransformationUtility tu2 = getNewTestTransformationUtility();

        assertEquals(tu1.hashCode(), tu2.hashCode());
        assertEquals(tu1.relative("pom.xml"), tu1);
        assertNotEquals(tu1.hashCode(), tu2.hashCode());
        assertEquals(tu2.relative("pom.xml"), tu2);
        assertEquals(tu1.hashCode(), tu2.hashCode());
        assertEquals(tu1.setName("TUA"), tu1);
        assertNotEquals(tu1.hashCode(), tu2.hashCode());
        assertEquals(tu2.setName("TUA"), tu2);
        assertEquals(tu1.hashCode(), tu2.hashCode());
    }

    @Test
    public void equalsTest() {
        TransformationUtility tu1 = getNewTestTransformationUtility();
        TransformationUtility tu2 = getNewTestTransformationUtility();

        assertEquals(tu1, tu2);
        assertEquals(tu1.relative("pom.xml"), tu1);
        assertNotEquals(tu1, tu2);
        assertEquals(tu2.relative("pom.xml"), tu2);
        assertEquals(tu1, tu2);
        assertEquals(tu1.setName("TUA"), tu1);
        assertNotEquals(tu1, tu2);
        assertEquals(tu2.setName("TUA"), tu2);
        assertEquals(tu1, tu2);
    }

    private UtilityCondition getNewUtilityCondition(boolean result) {
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
    private TransformationUtility getNewTestTransformationUtility() {
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

    private TransformationTemplate getNewTestTransformationTemplate() {
        return new TransformationTemplate() {
            @Override
            public Class<? extends Extension> getExtensionClass() {
                return null;
            }
            @Override
            public String getDescription() {
                return "Test transformation template";
            }
        };
    }

}
