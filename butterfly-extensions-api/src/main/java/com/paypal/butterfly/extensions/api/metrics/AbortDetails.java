package com.paypal.butterfly.extensions.api.metrics;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * POJO describing a transformation abort.
 *
 * @author facarvalho
 */
public class AbortDetails {

    private String utilityName;
    private String abortMessage;
    private String exceptionClass;
    private String exceptionMessage;
    private String exceptionStackTrace;

    public AbortDetails(Exception ex, String abortMessage, String utilityName) {
        if (ex == null) {
            throw new IllegalArgumentException("Exception object cannot be null");
        }
        if (utilityName == null || utilityName.trim().equals("")) {
            throw new IllegalArgumentException("Utility name cannot be null");
        }

        this.utilityName = utilityName;
        this.abortMessage = abortMessage;
        this.exceptionClass = ex.getClass().getSimpleName();
        this.exceptionMessage = ex.getMessage();
        this.exceptionStackTrace = ExceptionUtils.getStackTrace(ex);
    }

    public String getUtilityName() {
        return utilityName;
    }

    public String getAbortMessage() {
        return abortMessage;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getExceptionStackTrace() {
        return exceptionStackTrace;
    }

}