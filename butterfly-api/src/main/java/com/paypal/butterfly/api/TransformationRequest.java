package com.paypal.butterfly.api;

/**
 * This interface represents a transformation request, providing meta-data about
 * the application to be transformed.
 *
 * @author facarvalho
 */
public interface TransformationRequest {

    /**
     * Returns Butterfly version
     *
     * @return Butterfly version
     */
    String getButterflyVersion();

    /**
     * Returns the transformation request date in "yyyyy-mm-dd hh:mm:ss"
     *
     * @return the transformation request date in "yyyyy-mm-dd hh:mm:ss"
     */
    String getDateTime();

    /**
     * Returns the transformation request date in milliseconds
     *
     * @return the transformation request date in milliseconds
     */
    long getTimestamp();

    /**
     * Returns information about the application to be transformed
     *
     * @return information about the application to be transformed
     */
    Application getApplication();

    /**
     * Returns the configuration object associated with this transformation request
     *
     * @return the configuration object associated with this transformation request
     */
    Configuration getConfiguration();

    /**
     * Returns the name of the Butterfly extension used in this transformation request
     *
     * @return the name of the Butterfly extension used in this transformation request
     */
    String getExtensionName();

    /**
     * Returns the version of the Butterfly extension used in this transformation request
     *
     * @return the version of the Butterfly extension used in this transformation request
     */
    String getExtensionVersion();

    /**
     * Returns the name of the transformation template set in this transformation request.
     *
     * @return the name of the transformation template set in this transformation request
     */
    String getTemplateName();

    /**
     * Returns the name of the transformation template class set in this transformation request
     *
     * @return the name of the transformation template class set in this transformation request
     */
    String getTemplateClassName();

    /**
     * Returns true if the transformation template used in this transformation request is an upgrade step,
     * or false, if it is a regular transformation template.
     * See {@link com.paypal.butterfly.extensions.api.upgrade.UpgradeStep}.
     * See {@link com.paypal.butterfly.extensions.api.TransformationTemplate}.
     *
     * @return true if the transformation template used in this transformation request is an upgrade step
     */
    boolean isUpgradeStep();

    /**
     * Returns true if the transformation template used in this transformation request is blank.
     * See {@link com.paypal.butterfly.extensions.api.TransformationTemplate#setBlank(boolean)}.
     *
     * @return true if the transformation template used in this transformation request is blank
     */
    boolean isBlank();

}
