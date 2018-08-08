package com.paypal.butterfly.core;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.paypal.butterfly.api.AbortDetails;

/**
 * POJO describing a transformation abort.
 *
 * @author facarvalho
 */
class AbortDetailsImpl implements AbortDetails {

    private String templateName;
    private String templateClassName;
    private String utilityName;
    private String utilityClassName;
    private String abortMessage;
    private String exceptionClassName;
    private String exceptionMessage;
    private String exceptionStackTrace;

    AbortDetailsImpl(Exception ex, String abortMessage, String templateName, String templateClassName, String utilityName, String utilityClassName) {
        if (ex == null) {
            throw new IllegalArgumentException("Exception object cannot be null");
        }
        if (utilityName == null || utilityName.trim().equals("")) {
            throw new IllegalArgumentException("Utility name cannot be null");
        }

        this.templateName = templateName;
        this.templateClassName = templateClassName;
        this.utilityName = utilityName;
        this.utilityClassName = utilityClassName;
        this.abortMessage = abortMessage;
        this.exceptionClassName = ex.getClass().getName();
        this.exceptionMessage = ex.getMessage();
        this.exceptionStackTrace = ExceptionUtils.getStackTrace(ex);
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public String getTemplateClassName() {
        return templateClassName;
    }

    @Override
    public String getUtilityName() {
        return utilityName;
    }

    @Override
    public String getUtilityClassName() {
        return utilityClassName;
    }

    @Override
    public String getAbortMessage() {
        return abortMessage;
    }

    @Override
    public String getExceptionClassName() {
        return exceptionClassName;
    }

    @Override
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Override
    public String getExceptionStackTrace() {
        return exceptionStackTrace;
    }

}