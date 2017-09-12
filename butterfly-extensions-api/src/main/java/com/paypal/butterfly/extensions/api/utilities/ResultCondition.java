package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.*;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Checks the perform result, and optionally execution result as well, of a {@link TransformationUtility} instance.
 * If a result for the specified utility name is not found, this condition returns {@code false}, but with a warning.
 *
 * @author facarvalho
 */
public class ResultCondition extends UtilityCondition<ResultCondition> {

    private static final String DESCRIPTION = "Check if the perform result of utility '%s' is one of %s";

    private String utilityName;

    private PerformResult.Type[] performResultTypes;

    private TUExecutionResult.Type[] tuExecutionResultTypes;

    private TOExecutionResult.Type[] toExecutionResultTypes;

    public ResultCondition() {
    }

    public ResultCondition(String utilityName) {
        setUtilityName(utilityName);
    }

    public ResultCondition setUtilityName(String utilityName) {
        checkForBlankString("utilityName", utilityName);
        this.utilityName = utilityName;
        return this;
    }

    public ResultCondition setPerformResultTypes(PerformResult.Type... performResultTypes) {
        checkForNull("performResultTypes", performResultTypes);
        if (performResultTypes.length == 0) {
            throw new TransformationDefinitionException("No result types have been specified");
        }
        this.performResultTypes = performResultTypes;
        return this;
    }

    public ResultCondition setTuExecutionResultTypes(TUExecutionResult.Type... tuExecutionResultTypes) {
        checkForNull("tuExecutionResultTypes", tuExecutionResultTypes);
        if (tuExecutionResultTypes.length == 0) {
            throw new TransformationDefinitionException("No result types have been specified");
        }
        this.tuExecutionResultTypes = tuExecutionResultTypes;
        return this;
    }

    public ResultCondition setToExecutionResultTypes(TOExecutionResult.Type... toExecutionResultTypes) {
        checkForNull("toExecutionResultTypes", toExecutionResultTypes);
        if (toExecutionResultTypes.length == 0) {
            throw new TransformationDefinitionException("No result types have been specified");
        }
        this.toExecutionResultTypes = toExecutionResultTypes;
        return this;
    }

    public String getUtilityName() {
        return utilityName;
    }

    public PerformResult.Type[] getPerformResultTypes() {
        return performResultTypes.clone();
    }

    public TUExecutionResult.Type[] getTuExecutionResultTypes() {
        return tuExecutionResultTypes.clone();
    }

    public TOExecutionResult.Type[] getToExecutionResultTypes() {
        return toExecutionResultTypes.clone();
    }

    @Override
    public String getDescription() {
        String s1 = ( performResultTypes == null || performResultTypes.length == 0 ? "" : " perform" + Arrays.toString(performResultTypes));
        String s2 = ( tuExecutionResultTypes == null || tuExecutionResultTypes.length == 0 ? "" : " execution_tu" + Arrays.toString(tuExecutionResultTypes));
        String s3 = ( toExecutionResultTypes == null || toExecutionResultTypes.length == 0 ? "" : " execution_to" + Arrays.toString(toExecutionResultTypes));
        String possibleResultTypes = String.format("{%s%s%s }", s1, s2, s3);

        return String.format(DESCRIPTION, utilityName, possibleResultTypes);
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        PerformResult performResult = transformationContext.getResult(utilityName);

        if (performResult == null) {
            String warningMessage = "There is no utility performResult associated with name " + utilityName;
            return TUExecutionResult.warning(this, warningMessage, false);
        }

        PerformResult.Type performResultType = performResult.getType();
        boolean conditionResult = Arrays.asList(performResultTypes).contains(performResultType);

        if (conditionResult && performResultType.equals(PerformResult.Type.EXECUTION_RESULT)) {
            Object executionResultType = performResult.getExecutionResult().getType();
            List validExecutionTypes = Arrays.asList(ArrayUtils.addAll(tuExecutionResultTypes, toExecutionResultTypes));
            conditionResult = validExecutionTypes.contains(executionResultType);
        }

        return TUExecutionResult.value(this, conditionResult);
    }

}
