package com.paypal.butterfly.api;

/**
 * POJO describing a transformation abort in details.
 *
 * @author facarvalho
 */
public interface AbortDetails {

    /**
     * Returns the name of the transformation template that caused the transformation abort.
     *
     * @return the name of the transformation template that caused the transformation abort.
     */
    String getTemplateName();

    /**
     * Returns the name of the transformation template class that caused the transformation abort.
     *
     * @return the name of the transformation template class that caused the transformation abort.
     */
    String getTemplateClassName();

    /**
     * Returns the name of the Transformation Utility that caused the abort
     *
     * @return the name of the Transformation Utility that caused the abort
     */
    String getUtilityName();

    /**
     * Returns the name of the transformation utility class that caused the abort
     *
     * @return the name of the transformation utility class that caused the abort
     */
    String getUtilityClassName();

    /**
     * Returns the abort message
     *
     * @return the abort message
     */
    String getAbortMessage();

    /**
     * Returns the class of the exception that caused the transformation abort
     *
     * @return the class of the exception that caused the transformation abort
     */
    String getExceptionClassName();

    /**
     * Returns the message of the exception that caused the transformation abort
     *
     * @return the message of the exception that caused the transformation abort
     */
    String getExceptionMessage();

    /**
     * Returns a String representation of the stack trace related to the exception
     * that caused the transformation abort
     *
     * @return a String representation of the stack trace related to the exception
     * that caused the transformation abort
     */
    String getExceptionStackTrace();

}
