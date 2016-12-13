package com.paypal.butterfly.extensions.api.metrics;

/**
 * POJO containing statistics and meta-data about
 * the result of a transformation execution
 *
 * @author facarvalho
 */
public interface TransformationMetrics {

    /**
     * Returns Butterfly version
     *
     * @return Butterfly version
     */
    String getButterflyVersion();

    /**
     * Returns the transformation template name
     *
     * @return the transformation template name
     */
    String getTemplateName();

    /**
     * Returns the transformation date in "yyyyy-mm-dd hh:mm:ss"
     *
     * @return the transformation date in "yyyyy-mm-dd hh:mm:ss"
     */
    String getDateTime();

    /**
     * Returns the transformation date in milliseconds
     *
     * @return the transformation date in milliseconds
     */
    long getTimestamp();

    /**
     * Returns the OS id of the user who performed the transformation
     *
     * @return the OS id of the user who performed the transformation
     */
    String getUserId();

    /**
     * Returns the type of the transformed application
     *
     * @return the type of the transformed application
     */
    String getApplicationType();

    /**
     * Returns the name of the transformed application
     *
     * @return the name of the transformed application
     */
    String getApplicationName();

    /**
     * Returns the version the application was upgraded from.
     * It returns null if the transformation template is not
     * an upgrade template
     *
     * @return the version the application was upgraded from.
     * It returns null if the transformation template is not
     * an upgrade template
     */
    String getFromVersion();

    /**
     * Returns the version the application was upgraded to.
     * It returns null if the transformation template is not
     * an upgrade template
     *
     * @return the version the application was upgraded to.
     * It returns null if the transformation template is not
     * an upgrade template
     */
    String getToVersion();

    /**
     * Returns true if the transformed application requires
     * manual instructions to complete the transformation
     *
     * @return true if the transformed application requires
     * manual instructions to complete the transformation
     */
    boolean isRequiresManualInstructions();

    /**
     * @return true if the transformation was completed
     * successfully, even if it had warnings and non-fatal errors.
     * Returns false only if the transformation
     * was aborted due to fatal errors.
     */
    boolean isSuccessfulTransformation();

    /**
     * Returns transformation statistics
     *
     * @return transformation statistics
     */
    TransformationStatistics getStatistics();

    /**
     * Returns an identifier used to correlate
     * all upgrade steps part of the same upgrade path.
     * If the transformation is not an upgrade, then
     * it returns null
     *
     * @return an identifier used to correlate
     * all upgrade steps part of the same upgrade path
     */
    String getUpgradeCorrelationId();

}
