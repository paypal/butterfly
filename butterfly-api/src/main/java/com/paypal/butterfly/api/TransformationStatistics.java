package com.paypal.butterfly.api;

/**
 * POJO containing statistics about
 * the result of a transformation template execution
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

}
