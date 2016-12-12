package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.*;
import com.paypal.butterfly.extensions.api.metrics.TransformationStatistics;

/**
 * POJO to hold transformation statistics
 *
 * @author facarvalho
 */
class TransformationStatisticsImpl implements TransformationStatistics {

    // Number of utilities performed
    private int utilitiesCount = 0;

    // Number of operations performed
    private int operationsCount = 0;

    // Statistics per perform result
    private int performResultErrorCount = 0;
    private int performResultExecutionResultCount = 0;
    private int performResultSkippedConditionCount = 0;
    private int performResultSkippedDependencyCount = 0;

    // Statistics per execution result for TUs
    private int tuExecutionResultNullCount = 0;
    private int tuExecutionResultValueCount = 0;
    private int tuExecutionResultWarningCount = 0;
    private int tuExecutionResultErrorCount = 0;

    // Statistics per execution result for TOs
    private int toExecutionResultNoOpCount = 0;
    private int toExecutionResultSuccessCount = 0;
    private int toExecutionResultWarningCount = 0;
    private int toExecutionResultErrorCount = 0;

    // Number of necessary manual instructions (if any)
    private int manualInstructionsCount = 0;

    void registerResult(PerformResult result) {

        ExecutionResult executionResult = null;

        switch (result.getType()) {
            case ERROR:
                performResultErrorCount ++;
                break;
            case EXECUTION_RESULT:
                performResultExecutionResultCount++;
                executionResult = result.getExecutionResult();
                break;
            case SKIPPED_CONDITION:
                performResultSkippedConditionCount++;
                break;
            case SKIPPED_DEPENDENCY:
                performResultSkippedDependencyCount++;
                break;
        }

        TransformationUtility source = result.getSource();

        if (source instanceof TransformationOperation) {
            operationsCount++;
            if (executionResult != null) {
                TOExecutionResult toExecutionResult = (TOExecutionResult) executionResult;
                switch (toExecutionResult.getType()) {
                    case NO_OP:
                        toExecutionResultNoOpCount++;
                        break;
                    case SUCCESS:
                        toExecutionResultSuccessCount++;
                        break;
                    case WARNING:
                        toExecutionResultWarningCount++;
                        break;
                    case ERROR:
                        toExecutionResultErrorCount++;
                        break;
                }
            }
        } else {
            utilitiesCount++;
            if (executionResult != null) {
                TUExecutionResult tuExecutionResult = (TUExecutionResult) executionResult;
                switch (tuExecutionResult.getType()) {
                    case NULL:
                        tuExecutionResultNullCount++;
                        break;
                    case VALUE:
                        tuExecutionResultValueCount++;
                        break;
                    case WARNING:
                        tuExecutionResultWarningCount++;
                        break;
                    case ERROR:
                        tuExecutionResultErrorCount++;
                        break;
                }
            }
        }
    }

    void addManualInstruction() {
        manualInstructionsCount++;
    }

    @Override
    public int getUtilitiesCount() {
        return utilitiesCount;
    }

    @Override
    public int getOperationsCount() {
        return operationsCount;
    }

    @Override
    public int getPerformResultErrorCount() {
        return performResultErrorCount;
    }

    @Override
    public int getPerformResultExecutionResultCount() {
        return performResultExecutionResultCount;
    }

    @Override
    public int getPerformResultSkippedConditionCount() {
        return performResultSkippedConditionCount;
    }

    @Override
    public int getPerformResultSkippedDependencyCount() {
        return performResultSkippedDependencyCount;
    }

    @Override
    public int getTUExecutionResultNullCount() {
        return tuExecutionResultNullCount;
    }

    @Override
    public int getTUExecutionResultValueCount() {
        return tuExecutionResultValueCount;
    }

    @Override
    public int getTUExecutionResultWarningCount() {
        return tuExecutionResultWarningCount;
    }

    @Override
    public int getTUExecutionResultErrorCount() {
        return tuExecutionResultErrorCount;
    }

    @Override
    public int getTOExecutionResultNoOpCount() {
        return toExecutionResultNoOpCount;
    }

    @Override
    public int getTOExecutionResultSuccessCount() {
        return toExecutionResultSuccessCount;
    }

    @Override
    public int getTOExecutionResultWarningCount() {
        return toExecutionResultWarningCount;
    }

    @Override
    public int getTOExecutionResultErrorCount() {
        return toExecutionResultErrorCount;
    }

    @Override
    public int getManualInstructionsCount() {
        return manualInstructionsCount;
    }

}
