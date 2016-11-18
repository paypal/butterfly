package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.ExecutionResult;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;
import java.net.URL;

/**
 * This utility allows the creation of a manual instruction, which can be seen as
 * a transformation operation that is too complex to be automated, but still,
 * Butterfly should be aware of it, so that it can state to the user in the
 * end of the transformation.
 * </br>
 * Every manual instruction is reported to the user in order in the end of the transformation.
 *
 * @author facarvalho
 */
public class ManualInstruction extends TransformationUtility<ManualInstruction> {

    private String description;
    private String resourceName;

    public ManualInstruction(String description, String resourceName) {
        setSaveResult(false);

        setDescription(description);
        setResourceName(resourceName);
    }

    public ManualInstruction setDescription(String description) {
        checkForBlankString("description", description);
        this.description = description;
        return this;
    }

    public ManualInstruction setResourceName(String resourceName) {
        checkForBlankString("resourceName", resourceName);
        this.resourceName = resourceName;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Returns the instruction resource name
     *
     * @return the instruction resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    @Override
    protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        TUExecutionResult result = null;

        URL resource = getClass().getClassLoader().getResource(resourceName);
        if (resource == null) {
            String exceptionMessage = String.format("Resource %s could not be found in the classpath", resourceName);
            TransformationUtilityException e = new TransformationUtilityException(exceptionMessage);
            result = TUExecutionResult.error(this, e);
        } else {
            ManualInstructionRecord manualInstructionRecord = new ManualInstructionRecord(description, resource);
            result = TUExecutionResult.value(this, manualInstructionRecord);
        }

        return result;
    }

}
