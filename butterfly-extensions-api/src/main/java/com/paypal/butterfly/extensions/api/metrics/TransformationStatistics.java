package com.paypal.butterfly.extensions.api.metrics;

/**
 * Transformation statistics
 *
 * @author facarvalho
 */
public interface TransformationStatistics {

    /**
     * @return how many transformation utilities (not counting transformation operations)
     * were executed during the transformation
     */
    int getUtilitiesCount();

    /**
     * @return how many transformation operations
     * were executed during the transformation
     */
    int getOperationsCount();

    /**
     * @return how many transformation utilities and operations
     * resulted in perform error
     */
    int getPerformResultErrorCount();

    /**
     * @return how many transformation utilities and operations
     * resulted in an execution result
     */
    int getPerformResultExecutionResultCount();

    /**
     * @return how many transformation utilities and operations
     * were skipped due to one or more conditions being false
     */
    int getPerformResultSkippedConditionCount();

    /**
     * @return how many transformation utilities and operations
     * were skipped due to not meeting one or more dependencies
     */
    int getPerformResultSkippedDependencyCount();

    /**
     * @return how many transformation utilities
     * returned null
     */
    int getTUExecutionResultNullCount();

    /**
     * @return how many transformation utilities
     * returned a value different than null
     */
    int getTUExecutionResultValueCount();

    /**
     * @return how many transformation utilities
     * returned a warning execution result
     */
    int getTUExecutionResultWarningCount();

    /**
     * @return how many transformation utilities
     * returned an execution error
     */
    int getTUExecutionResultErrorCount();

    /**
     * @return how many transformation operations
     * resulted in an no-op
     */
    int getTOExecutionResultNoOpCount();

    /**
     * @return how many transformation operations
     * successfully performed a change
     */
    int getTOExecutionResultSuccessCount();

    /**
     * @return how many transformation operations
     * returned a warning execution result
     */
    int getTOExecutionResultWarningCount();

    /**
     * @return how many transformation operations
     * returned an execution error
     */
    int getTOExecutionResultErrorCount();

    /**
     * @return how many manual instructions are
     * necessary to complete the transformation
     */
    int getManualInstructionsCount();

    /**
     * @return the total of transformation utilities and
     * operations performed (regardless of their results)
     */
    int getPerformTotal();

    /**
     * @return the total of skipped transformation utilities and
     * operations
     */
    int getSkippedTotal();

    /**
     * @return the total of transformation utilities and
     * operations executed. It does not include the ones that were
     * skipped, or that resulted in perform error (which is different
     * than execution error)
     */
    int getExecutionTotal();

    /**
     * @return the total of transformation operations
     * executed. It does not include the ones that were
     * skipped, or that resulted in perform error (which is different
     * than execution error)
     */
    int getTOExecutionTotal();

    /**
     * @return the total of transformation utilities
     * executed. It does not include the ones that were
     * skipped, or that resulted in perform error (which is different
     * than execution error)
     */
    int getTUExecutionTotal();

    /**
     * @return the total of transformation utilities
     * and operations that resulted in an error when executed.
     * It does not include the ones that were
     * skipped, or that resulted in perform error (which is different
     * than execution error)
     */
    int getExecutionErrorTotal();

    /**
     * @return the percentage of transformation utilities and operations
     * that resulted in perform error
     */
    float getPerformErrorRate();

    /**
     * @return the percentage of transformation utilities
     * and operations that resulted in an error when executed.
     * It does not include the ones that were
     * skipped, or that resulted in perform error (which is different
     * than execution error)
     */
    float getExecutionErrorRate();

    /**
     * @return the percentage of transformation operations
     * that returned an execution error
     */
    float getTOExecutionErrorRate();

    /**
     * @return the percentage of transformation operations
     * that returned a warning execution result
     */
    float getTOExecutionWarningRate();

    /**
     * @return the percentage of transformation utilities
     * that returned an execution error
     */
    float getTUExecutionErrorRate();

    /**
     * @return the percentage of transformation utilities
     * that returned a warning execution result
     */
    float getTUExecutionWarningRate();

}
