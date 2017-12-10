package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.*;
import com.paypal.butterfly.extensions.api.metrics.TransformationStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * POJO to hold transformation statistics
 *
 * @author facarvalho
 */
class TransformationStatisticsImpl implements TransformationStatistics {

    private static Logger logger = LoggerFactory.getLogger(TransformationStatisticsImpl.class);

    // Number of utilities performed
    private int utilitiesCount = 0;

    // Number of operations performed
    private int operationsCount = 0;
    
    // Perform results
    private PerformResults performResults = new PerformResults();

    // Execution results
    private ExecutionResults executionResults = new ExecutionResults();

    // Number of necessary manual instructions (if any)
    private int manualInstructionsCount = 0;

    void registerResult(PerformResult result) {

        ExecutionResult executionResult = null;

        switch (result.getType()) {
            case ERROR:
                performResults.errorCount ++;
                break;
            case EXECUTION_RESULT:
                performResults.executionResultCount++;
                executionResult = result.getExecutionResult();
                break;
            case SKIPPED_CONDITION:
                performResults.skippedConditionCount++;
                break;
            case SKIPPED_DEPENDENCY:
                performResults.skippedDependencyCount++;
                break;
            default:
                logger.error("Unknown result type {}", result.getType());
                break;
        }

        TransformationUtility source = result.getSource();

        if (source instanceof TransformationOperation) {
            operationsCount++;
            if (executionResult != null) {
                TOExecutionResult toExecutionResult = (TOExecutionResult) executionResult;
                switch (toExecutionResult.getType()) {
                    case NO_OP:
                        executionResults.operations.noOpCount++;
                        break;
                    case SUCCESS:
                        executionResults.operations.successCount++;
                        break;
                    case WARNING:
                        executionResults.operations.warningCount++;
                        break;
                    case ERROR:
                        executionResults.operations.errorCount++;
                        break;
                    default:
                        logger.error("Unknown result type {}", result.getType());
                        break;
                }
            }
        } else {
            utilitiesCount++;
            if (executionResult != null) {
                TUExecutionResult tuExecutionResult = (TUExecutionResult) executionResult;
                switch (tuExecutionResult.getType()) {
                    case NULL:
                        executionResults.utilities.nullCount++;
                        break;
                    case VALUE:
                        executionResults.utilities.valueCount++;
                        break;
                    case WARNING:
                        executionResults.utilities.warningCount++;
                        break;
                    case ERROR:
                        executionResults.utilities.errorCount++;
                        break;
                    default:
                        logger.error("Unknown result type {}", result.getType());
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
        return performResults.errorCount;
    }

    @Override
    public int getPerformResultExecutionResultCount() {
        return performResults.executionResultCount;
    }

    @Override
    public int getPerformResultSkippedConditionCount() {
        return performResults.skippedConditionCount;
    }

    @Override
    public int getPerformResultSkippedDependencyCount() {
        return performResults.skippedDependencyCount;
    }

    @Override
    public int getTUExecutionResultNullCount() {
        return executionResults.utilities.nullCount;
    }

    @Override
    public int getTUExecutionResultValueCount() {
        return executionResults.utilities.valueCount;
    }

    @Override
    public int getTUExecutionResultWarningCount() {
        return executionResults.utilities.warningCount;
    }

    @Override
    public int getTUExecutionResultErrorCount() {
        return executionResults.utilities.errorCount;
    }

    @Override
    public int getTOExecutionResultNoOpCount() {
        return executionResults.operations.noOpCount;
    }

    @Override
    public int getTOExecutionResultSuccessCount() {
        return executionResults.operations.successCount;
    }

    @Override
    public int getTOExecutionResultWarningCount() {
        return executionResults.operations.warningCount;
    }

    @Override
    public int getTOExecutionResultErrorCount() {
        return executionResults.operations.errorCount;
    }

    @Override
    public int getManualInstructionsCount() {
        return manualInstructionsCount;
    }

    // Statistics per perform result
    private static class PerformResults {
        private int errorCount = 0;
        private int executionResultCount = 0;
        private int skippedConditionCount = 0;
        private int skippedDependencyCount = 0;
    }
    
    private static class ExecutionResults {
        private TU utilities = new TU();
        private TO operations = new TO();

        // Statistics per execution result for TUs
        private static class TU {
            private int nullCount = 0;
            private int valueCount = 0;
            private int warningCount = 0;
            private int errorCount = 0;
        }

        // Statistics per execution result for TOs
        private static class TO {
            private int noOpCount = 0;
            private int successCount = 0;
            private int warningCount = 0;
            private int errorCount = 0;
        }
    }
    
}
