package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.PerformResult;
import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Condition to check the result of an utility. If a result for the specified
 * utility name is not found, this condition returns as false with a warning
 *
 * @author facarvalho
 */
public class ResultCondition extends UtilityCondition<ResultCondition> {

    private static final String DESCRIPTION = "Check if the perform result of utility '%s' is one of %s";

    private String utilityName;

    private PerformResult.Type[] resultTypes;

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

    public ResultCondition setResultTypes(PerformResult.Type... resultTypes) {
        checkForNull("resultTypes", resultTypes);
        if (resultTypes.length == 0) {
            throw new TransformationDefinitionException("No result types have been specified");
        }
        this.resultTypes = resultTypes;
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

    public PerformResult.Type[] getResultTypes() {
        return resultTypes.clone();
    }

    public TUExecutionResult.Type[] getTuExecutionResultTypes() {
        return tuExecutionResultTypes.clone();
    }

    public TOExecutionResult.Type[] getToExecutionResultTypes() {
        return toExecutionResultTypes.clone();
    }

    @Override
    public String getDescription() {
        String s1 = ( resultTypes == null || resultTypes.length == 0 ? "" : " perform" + Arrays.toString(resultTypes));
        String s2 = ( tuExecutionResultTypes == null || tuExecutionResultTypes.length == 0 ? "" : " execution_tu" + Arrays.toString(tuExecutionResultTypes));
        String s3 = ( toExecutionResultTypes == null || toExecutionResultTypes.length == 0 ? "" : " execution_to" + Arrays.toString(toExecutionResultTypes));
        String possibleResultTypes = String.format("{%s%s%s }", s1, s2, s3);

        return String.format(DESCRIPTION, utilityName, possibleResultTypes);
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        PerformResult result = transformationContext.getResult(utilityName);

        if (result == null) {
            String warningMessage = "There is no utility result associated with name " + utilityName;
            return TUExecutionResult.warning(this, false, warningMessage);
        }

        PerformResult.Type resultType = result.getType();
        boolean conditionResult = Arrays.asList(resultTypes).contains(resultType);

        if (conditionResult && resultType.equals(PerformResult.Type.EXECUTION_RESULT)) {
            Object executionResultType = result.getExecutionResult().getType();
            List validExecutionTypes = Arrays.asList(ArrayUtils.addAll(tuExecutionResultTypes, toExecutionResultTypes));
            conditionResult = validExecutionTypes.contains(executionResultType);
        }

        return TUExecutionResult.value(this, conditionResult);
    }

}
