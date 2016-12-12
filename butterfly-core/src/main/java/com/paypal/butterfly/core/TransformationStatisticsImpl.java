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

    // Totals
    private int performTotal = 0;
    private int skippedTotal = 0;
    private int executionTotal = 0;
    private int toExecutionTotal = 0;
    private int tuExecutionTotal = 0;
    private int executionErrorTotal = 0;

    // Rates
    private float performErrorRate = 0;
    private float executionErrorRate = 0;
    private float toExecutionErrorRate = 0;
    private float toExecutionWarningRate = 0;
    private float tuExecutionErrorRate = 0;
    private float tuExecutionWarningRate = 0;

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

        updateTotals();
        updateRates();
    }

    private void updateTotals() {
        performTotal = utilitiesCount + operationsCount;
        skippedTotal = performResultSkippedConditionCount + performResultSkippedDependencyCount;
        toExecutionTotal = toExecutionResultErrorCount + toExecutionResultNoOpCount + toExecutionResultSuccessCount + toExecutionResultWarningCount;
        tuExecutionTotal = tuExecutionResultErrorCount + tuExecutionResultNullCount + tuExecutionResultValueCount + tuExecutionResultWarningCount;
        executionTotal = tuExecutionTotal + toExecutionTotal;
        executionErrorTotal = toExecutionResultErrorCount + tuExecutionResultErrorCount;
    }

    private void updateRates() {
        if (performTotal > 0) performErrorRate = 100 * (float) performResultErrorCount / performTotal;
        if (executionTotal > 0) executionErrorRate = 100 * (float) executionErrorTotal / executionTotal;
        if (toExecutionTotal > 0) toExecutionErrorRate = 100 * (float) toExecutionResultErrorCount / toExecutionTotal;
        if (toExecutionTotal > 0) toExecutionWarningRate = 100 * (float) toExecutionResultWarningCount / toExecutionTotal;
        if (tuExecutionTotal > 0) tuExecutionErrorRate = 100 * (float) tuExecutionResultErrorCount / tuExecutionTotal;
        if (tuExecutionTotal > 0) tuExecutionWarningRate =  100 * (float) tuExecutionResultWarningCount / tuExecutionTotal;
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

    @Override
    public int getPerformTotal() {
        return performTotal;
    }

    @Override
    public int getSkippedTotal() {
        return skippedTotal;
    }

    @Override
    public int getExecutionTotal() {
        return executionTotal;
    }

    @Override
    public int getTOExecutionTotal() {
        return toExecutionTotal;
    }

    @Override
    public int getTUExecutionTotal() {
        return tuExecutionTotal;
    }

    @Override
    public int getExecutionErrorTotal() {
        return executionErrorTotal;
    }

    @Override
    public float getPerformErrorRate() {
        return performErrorRate;
    }

    @Override
    public float getExecutionErrorRate() {
        return executionErrorRate;
    }

    @Override
    public float getTOExecutionErrorRate() {
        return toExecutionErrorRate;
    }

    @Override
    public float getTOExecutionWarningRate() {
        return toExecutionWarningRate;
    }

    @Override
    public float getTUExecutionErrorRate() {
        return tuExecutionErrorRate;
    }

    @Override
    public float getTUExecutionWarningRate() {
        return tuExecutionWarningRate;
    }

}
